package com.yfc.com.yfc.socket

import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val DEBUG = true

fun logE(error: Throwable, tag: String = "") = logE(error.message ?: "", tag)
fun logE(error: String, tag: String = "") {
    if (DEBUG) println("${tag}:${error}")
}

fun Closeable?.closeSafe() {
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