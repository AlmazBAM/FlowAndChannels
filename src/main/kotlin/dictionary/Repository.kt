package dictionary

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.coroutines.resume

object Repository {

    private const val API_KEY = "18093frhU9WD+PEz15L8tA==WS6ylv2AgyvsUIEp"
    private const val BASE_URL = "https://api.api-ninjas.com/v1/dictionary"
    private const val HEADER_KEY = "X-Api-Key"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val client = OkHttpClient()


    fun loadDefinition(word: String, callback: (Result<Definition>) -> Unit) {

        val url = BASE_URL.toHttpUrl().newBuilder()
            .addQueryParameter("word", word)
            .build()
        val request = Request.Builder()
            .url(url)
            .addHeader(HEADER_KEY, API_KEY)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        response.body?.let { responseBody ->
                            callback(Result.success(json.decodeFromString<Definition>(responseBody.string())))
                        }
                    } else {
                        callback(Result.failure(IOException("Http Error: ${response.code}")))
                    }
                } catch (e: Exception) {
                    callback(Result.failure(e))
                } finally {
                    response.close()
                }

            }
        })
    }
}

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("Cathed exception $throwable")
}
private val scope = CoroutineScope(dispatcher + exceptionHandler)


fun main() {
    scope.launch {
        while (true) {
            print("Enter word or exit: ")
            val word = readln().trim()
            if (word == "exit") break
            if (word.isBlank()) {
                println("Enter a valid word")
                continue
            }
            val definition = loadData(word)
            println(definition)
        }
    }
}

private suspend fun loadData(word: String): Definition? {
    return suspendCancellableCoroutine { continuation ->
        Repository.loadDefinition(word) { result ->
            result.fold(
                onSuccess = {
                    continuation.resume(it)
                },
                onFailure = {
                    continuation.resume(null)
                }
            )
        }
        continuation.invokeOnCancellation {
            println("Request cancelled")
        }
    }
}