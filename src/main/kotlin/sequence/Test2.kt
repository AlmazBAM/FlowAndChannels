package sequence

import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    var filter = 0
    var map = 0
    val list = sequence<Int> {
        println("Start")
        repeat(100) {
            yield(Random.nextInt(1000))
        }
        println("End")
    }
//    val list = generateSequence {
//        Random.nextInt(1000)
//    }
//    val list = generateSequence(0) {
//        it + 1
//    }

    list
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
        .count()

    println("Filter = $filter, map = $map")
}
