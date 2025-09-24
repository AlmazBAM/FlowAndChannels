package channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)
private val channel = Channel<Int>(capacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST) {
    println("$it was deleted")
}

fun main() {
    scope.launch {
        repeat(100) {
            println("Channel 1 is sending")
            channel.send(1)
            println("Channel 1 was sent")
            delay(100)
        }
    }
    scope.launch {
        repeat(100) {
            println("Channel 2 is sending")
            channel.send(2)
            println("Channel 2 was sent")
            delay(100)
        }
    }

    scope.launch {
        channel.consumeEach {
            delay(1000)
            println("Consumer 1 receive $it")
        }
    }
    scope.launch {
        channel.consumeEach {
            delay(1000)
            println("Consumer 2 receive $it")
        }
    }

//    scope.launch {
//        val number = channel.receive()
//        println("Consumer 1 receive $number")
//    }
//    scope.launch {
//        val number = channel.receive()
//        println("Consumer 2 receive $number")
//    }
}