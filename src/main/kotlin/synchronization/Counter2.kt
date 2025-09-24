package synchronization

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors

class Counter2 {
    var value = 0
    private val mutex = Mutex()
    suspend fun inc() {
        mutex.withLock {
            delay(1)
            value++
        }
    }
}

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    val counter = Counter2()
    scope.launch {
        buildList {
            repeat(100) {
                scope.launch {
                    repeat(10) {
                        counter.inc()
                    }
                }.let { add(it) }
            }
        }.joinAll()
        println(counter.value)
    }

}