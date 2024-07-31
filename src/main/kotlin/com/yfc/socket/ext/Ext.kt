package com.yfc.com.yfc.socket.ext

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val DEBUG = true
private const val BASE_TAG = "SimpleSocket"
fun logE(throwable: Throwable, tag: String = BASE_TAG) = logE(throwable.message ?: "", tag)
fun logE(error: String, tag: String = BASE_TAG) {
    if (DEBUG) println("${tag}:${error}")
}

fun AutoCloseable?.closeSafe() {
    kotlin.runCatching {
        this?.close()
    }.onFailure {
        logE(it)
    }
}

fun getCachedPool(): ThreadPoolExecutor {
    return ThreadPoolExecutor(
        0,
        128,
        60L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(),
        Executors.defaultThreadFactory(),
    )
}

fun isMainThread(): Boolean {
    return true
}

fun runOnUiThread(listener: () -> Unit) {
    listener.invoke()
}

fun getFileSavedCacheDir(): String {
    return "D://test/".also { File(it).mkdirs() }
}