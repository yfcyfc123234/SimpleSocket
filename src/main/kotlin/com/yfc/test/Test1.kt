package test
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

object Test1 {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { test(10000000, 600) }
    }

    private suspend fun test(maxCount: Int, personCount: Int) = coroutineScope {
        val startTime = System.currentTimeMillis()
        val testCount = AtomicInteger(0)
        val list = mutableListOf<Deferred<Int>>()

        repeat((0 until maxCount).count()) {
            list.add(
                async(Dispatchers.Default) {
                    testCount.incrementAndGet()

                    testInside(personCount)
                }
            )
        }

        val results = list.awaitAll()

        val frequencyMap = results.groupingBy { it }.eachCount()
        val frequencyMapSorted = frequencyMap.toSortedMap { o1, o2 -> frequencyMap[o1]!!.compareTo(frequencyMap[o2]!!) }
        frequencyMapSorted.forEach { (num, freq) -> println("$num: $freq") }

        println("testCount=${testCount} testTime=${System.currentTimeMillis() - startTime}ms")
    }

    private fun testInside(personCount: Int): Int {
        val list = MutableList(personCount) { it + 1 }
        while (list.size > 1) {
            var index: Int
            while (Random.nextInt(0, list.size).also { index = it } % 2 != 0) {
            }
            list.removeAt(index)
        }
        return list.first()
    }
}