package com.yfc.com.yfc.socket

import com.yfc.com.yfc.socket.ext.*
import com.yfc.com.yfc.socket.helper.SocketHelper
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import kotlin.concurrent.thread
import kotlin.random.Random

class ClientCompat(
    val host: String,
    val port: Int,
    var receiveType: ReceiveType = ReceiveType.STRING_2048,
    var charset: Charset = Charsets.UTF_8,
    var listener: ClientSocketListener? = null,
) {
    companion object {
        private val USE_TYPE = UseType.CLIENT
    }

    val tag = ClientCompat::class.simpleName ?: ""

    var socketHelper: SocketHelper? = null

    fun connect() {
        thread { connectInside() }
    }

    private fun connectInside() {
        runCatching {
            socketHelper = SocketHelper(
                USE_TYPE,
                Socket(host, port),
                receiveType,
                charset,
                ListenerCompat(useType = USE_TYPE, clientListener = listener),
            )
        }.onFailure {
            logE(it, tag)
            runOnUiThread { listener?.onConnectFailed(it, this@ClientCompat) }
        }
    }

    fun sendFile(filePath: String) = socketHelper?.sendFile(File(filePath))
    fun sendFile(file: File) = socketHelper?.sendFile(file)

    fun sendDataRaw(dataRaw: ByteArray) = socketHelper?.sendDataRaw(dataRaw)
    fun sendMessage(message: String) = socketHelper?.sendMessage(message)
    fun sendMessage(message: SocketMessage) = socketHelper?.sendMessage(message)

    fun disconnect() {
        socketHelper?.disconnect()
    }

    fun isConnected(): Boolean = socketHelper?.isConnected() == true
}
