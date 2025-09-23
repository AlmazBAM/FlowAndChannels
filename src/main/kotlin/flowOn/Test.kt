package flowOn

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors

private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {

    scope.launch {
        getFlow().onStart {
                println("onStart ${getCurrentThread()}")
            }.onEach {
                println("onEach 1 ${getCurrentThread()}")
            }.flowOn(dispatcher).map {
                println("Map ${getCurrentThread()}")
                it
            }.flowOn(Dispatchers.Default).onEach {
                println("onEach 2 ${getCurrentThread()}")
            }.collect {
                println("Collected ${getCurrentThread()}")
            }
    }
}

private fun getFlow() = flow {
    var seconds = 0
    while (true) {
        println("Emitted: $seconds in ${getCurrentThread()}")
        emit(seconds++)
        delay(1000)
    }
}

private fun getCurrentThread() = Thread.currentThread().name