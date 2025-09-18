package sequence

import kotlin.random.Random

fun main() {
    var filter = 0
    var map = 0
    val list = mutableListOf<Int>().apply {
        repeat(1000) {
            add(Random.nextInt(100))
        }
    }.asSequence()
        .filter {
            println("Filter")
            filter++
            it % 2 == 0
        }
        .map {
            println("Map")
            map++
            it * 10
        }
        .take(10)
        .forEach(::println)

    println("Filter = $filter, map = $map")
}
