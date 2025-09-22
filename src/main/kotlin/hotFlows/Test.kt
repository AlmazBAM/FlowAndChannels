package hotFlows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    val flow = Repository.timer
    scope.launch {
        flow.collect {
            println("Coroutine 1: $it")
        }
    }
    scope.launch {
        delay(5000)
        flow.take(4).collect {
            println("Coroutine 2: $it")
        }
        println("Finished")
    }
}

