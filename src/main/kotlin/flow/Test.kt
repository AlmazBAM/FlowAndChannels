package flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private var lastIndex = 0
private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

private suspend fun loadNext(): List<String> {
    delay(2000)
    return (lastIndex..<(lastIndex + 10)).map {
        "Video: $it"
    }.also {
        lastIndex += 10
        println("Loaded: ${it.joinToString()}")
    }
}

private suspend fun scroll(videos: List<String>) {
    delay(videos.size * 100L)
    println("Scrolled: ${videos.joinToString()}}")
}


fun main() {
    val list1 = listOf(1, 2, 3).asFlow()
    val list2 = flowOf(1, 2, 3)
    scope.launch {
        flow {
            repeat(10) {
                val nextData = loadNext()
                emit(nextData)
            }
        }.collect { scroll(it) }
    }
}