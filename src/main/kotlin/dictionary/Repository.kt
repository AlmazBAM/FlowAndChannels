package dictionary

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import kotlin.coroutines.resume

object Repository {

    private const val API_KEY = "18093frhU9WD+PEz15L8tA==WS6ylv2AgyvsUIEp"
    private const val BASE_URL = "https://api.api-ninjas.com/v1/dictionary"
    private const val HEADER_KEY = "X-Api-Key"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val client = OkHttpClient()


    private fun loadDefinition(word: String, callback: (Result<List<String>>) -> Unit) {
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
                            callback(Result.success(json.decodeFromString<Definition>(responseBody.string()).mapDefinitionToList()))
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
    suspend fun loadData(word: String): List<String> {
        return suspendCancellableCoroutine { continuation ->
            loadDefinition(word) { result ->
                result.fold(
                    onSuccess = {
                        continuation.resume(it)
                    },
                    onFailure = {
                        continuation.resume(emptyList())
                    }
                )
            }
            continuation.invokeOnCancellation {
                println("Request cancelled")
            }
        }
    }

    private fun Definition.mapDefinitionToList(): List<String> {
        return this.definition.split(Regex("\\d. ")).map { it.trim() }.filter { it.isNotEmpty() }
    }
}