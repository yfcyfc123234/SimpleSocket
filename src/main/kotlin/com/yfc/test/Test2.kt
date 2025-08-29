package test

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

object Test2 {
    private const val BATCH_SIZE = 10000 // 每个批次处理的任务数量

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { test(10000000, 600) }
    }

    private suspend fun test(maxCount: Int, personCount: Int) = coroutineScope {
        val startTime = System.currentTimeMillis()
        val testCount = AtomicInteger(0)
        val frequencyMap = mutableMapOf<Int, Int>()

        for (i in 0 until maxCount step BATCH_SIZE) {
            val batchSize = minOf(BATCH_SIZE, maxCount - i)
            val list = mutableListOf<Deferred<Int>>()

            repeat(batchSize) {
                list.add(
                    async(Dispatchers.Default) {
                        testCount.incrementAndGet()
                        testInside(personCount)
                    }
                )
            }

            val results = list.awaitAll()
            results.forEach { result ->
                frequencyMap[result] = frequencyMap.getOrDefault(result, 0) + 1
            }
        }

        val frequencyMapSorted = frequencyMap.toSortedMap { o1, o2 -> frequencyMap[o1]!!.compareTo(frequencyMap[o2]!!) }
        frequencyMapSorted.forEach { (num, freq) -> println("$num: $freq") }

        println("testCount=${testCount} testTime=${System.currentTimeMillis() - startTime}ms")
    }

    //    private fun testInside(personCount: Int): Int {
//        val array = IntArray(personCount) { it + 1 }
//        var size = personCount
//
//        while (size > 1) {
//            val index = Random.nextInt(0, size / 2) * 2
//            // 将最后一个元素移到要移除的位置
//            array[index] = array[--size]
//        }
//
//        return array[0]
//    }
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