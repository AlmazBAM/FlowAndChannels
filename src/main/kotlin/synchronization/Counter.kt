package synchronization

import kotlin.concurrent.thread

class Counter {
    var value = 0

    private val lock = Any()

    fun inc() {
        synchronized(lock) {
            Thread.sleep(1)
            value++
        }
    }
}

fun main() {
    val counter = Counter()
    buildList {
        repeat(100) {
            thread {
                repeat(10) {
                    counter.inc()
                }
            }.let { add(it) }
        }
    }.forEach { it.join() }
    println(counter.value)

}