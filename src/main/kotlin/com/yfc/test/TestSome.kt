package com.yfc.test

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take

object TestSome {
    @JvmStatic
    fun main(args: Array<String>) {

        testasdasdasd()

//        GlobalScope.launch {
//            val shareIn = fibonacci().stateIn(this, SharingStarted.Eagerly, 0)
//
//            delay(1000)
//
//            (0 until 10).forEach {
//                launch { shareIn.test((it + 1).toString()) }
//            }
//        }
//
//        thread { TimeUnit.SECONDS.sleep(50) }
    }

    private fun testasdasdasd() {
        val message = "你收到28条新消息"
        val regex = Regex("^你收到\\s*\\d+\\s*条消息.*")
        println(regex.matches(message))
    }

    private suspend fun SharedFlow<Int>.test(add: String) = coroutineScope {
        delay(500)

        take(10).collect { println("${add}_$it") }
    }

    private suspend fun fibonacci(): Flow<Int> = flow {
        var x = 0
        while (true) {
            emit(x)
            x++
//            if (x >= 100) return@flow

            delay(200)
        }
    }
}