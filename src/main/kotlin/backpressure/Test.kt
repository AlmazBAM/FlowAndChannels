package backpressure

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
     val flow = flow {
         repeat(100) {
             println("Before emit: $it")
             emit(it)
             println("After emit: $it")
             delay(100)
         }
     }.buffer(capacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    scope.launch {
        flow.collect {
            println("Collected: $it")
            delay(1000)
            println(it)
        }
    }

}