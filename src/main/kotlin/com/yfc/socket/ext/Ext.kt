package com.yfc.socket.ext

import org.apache.log4j.BasicConfigurator
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val DEBUG = true
private const val BASE_TAG = "SimpleSocket"

private val loggerFactory by lazy {
    BasicConfigurator.configure()
    LoggerFactory.getILoggerFactory()
}

fun logD(any: Any? = null, tag: String = BASE_TAG) {
    any ?: return
    if (DEBUG) loggerFactory.getLogger(tag).debug("{}", any)
}

fun logW(any: Any? = null, tag: String = BASE_TAG) {
    any ?: return
    if (DEBUG) loggerFactory.getLogger(tag).warn("{}", any)
}

fun logE(any: Any? = null, tag: String = BASE_TAG) {
    any ?: return
    if (DEBUG) loggerFactory.getLogger(tag).error("{}", any)
}

fun AutoCloseable?.closeSafe() {
    runCatching {
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