import kotlinx.coroutines.*

object Test3 {
    private fun threadHashCode() = Thread.currentThread().let { it.name + " " + it.hashCode() }

    @JvmStatic
    fun main(args: Array<String>) {
        println("当前活动线程数量: ${Runtime.getRuntime().availableProcessors()}")



        runBlocking {
            (0 until 200).forEach { test(it) }
        }
    }

    private suspend fun test(index: Int) = coroutineScope {
        async(Dispatchers.Default) {
            delay(1000L)
            println("${index} async1 ${threadHashCode()}")
        }
        async(Dispatchers.Default){
            delay(2000L)
            println("${index} async2 ${threadHashCode()}")
        }

        println("${index}  ${threadHashCode()}")
    }
}