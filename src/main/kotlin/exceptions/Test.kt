package exceptions

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
    println("Catched exception")
}
private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher /*+ exceptionHandler*/)


fun main() {

    val flow = getFlow()
    scope.launch {
//        try {
//            flow.collect {
//                println(it)
//            }
//        } catch (e: Exception) {
//            println("Catched Exception $e")
//        }
        flow
            .retry(5) {
                true
            }
            .catch {
                println("Catched exceptions $it")
            }.collect {
                println(it)
            }
    }
}

private fun getFlow() = flow {
    repeat(5) {
        emit(it)
    }
    error("Exception in flow")
}