package com.yfc.com.yfc.socket.simple

import com.yfc.com.yfc.socket.ext.*
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class SimpleClient(private val host: String, private val port: Int) {
    val tag = SimpleClient::class.simpleName ?: ""

    private var socket: Socket? = null
    private var output: OutputStream? = null
    private var input: InputStream? = null

    var onConnectFailed: (() -> Unit)? = null
    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onMessageReceived: ((String) -> Unit)? = null

    fun connect() {
        thread {
            runCatching {
                socket = Socket(host, port)

                output = socket!!.getOutputStream()
                input = socket!!.getInputStream()

                isDisconnect = false

                runOnUiThread { onConnected?.invoke() }

                listen()
            }.onFailure {
                logE(it, tag)

                runOnUiThread { onConnectFailed?.invoke() }
            }
        }
    }

    fun sendMessage(message: String) {
        if (isMainThread()) {
            getCachedPool().submit { sendMessageInside(message) }
        } else {
            sendMessageInside(message)
        }
    }

    private fun sendMessageInside(message: String) {
        runCatching {
            output?.apply {
                write(message.toByteArray())
                flush()
                logD("sendMessage message=${message}", tag)
            }
        }.onFailure {
            logE(it, tag)
        }
    }

    private fun listen() {
        try {
            while (socket?.isConnected == true && !isDisconnect && input != null) {
                var l: Int
                val b = ByteArray(2048)
                while (input!!.read(b, 0, b.size).also { l = it } != -1) {
                    val message = String(b, 0, l)
                    logD("message=${message}", tag)
                    if (message.isNotEmpty()) {
                        runOnUiThread { onMessageReceived?.invoke(message) }
                    }
                }
            }
        } catch (e: Exception) {
            logE(e, tag)
        } finally {
            disconnect()
        }
    }

    private var isDisconnect = false

    fun disconnect() {
        if (isDisconnect) return
        isDisconnect = true

        onConnectFailed = null
        onConnected = null
        onMessageReceived = null

        socket.closeSafe()
        output.closeSafe()
        input.closeSafe()

        runOnUiThread {
            onDisconnected?.invoke()
            onDisconnected = null
        }
    }

    fun isConnected(): Boolean = socket?.isConnected == true
}
