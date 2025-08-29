package com.yfc.socket

import com.yfc.socket.ext.ListenerCompat
import com.yfc.socket.ext.ReceiveType
import com.yfc.socket.ext.ServerSocketListener
import com.yfc.socket.ext.SocketMessage
import com.yfc.socket.ext.UseType
import com.yfc.socket.ext.closeSafe
import com.yfc.socket.ext.logE
import com.yfc.socket.ext.runOnUiThread
import com.yfc.socket.helper.SocketHelper
import java.io.File
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlin.random.Random

class ServerCompat(
    var port: Int,
    val retryRandomPort: Boolean = true,
    var receiveType: ReceiveType = ReceiveType.STRING_2048,
    var charset: Charset = Charsets.UTF_8,
    var listener: ServerSocketListener? = null,
) {
    companion object {
        private val USE_TYPE = UseType.SERVER
    }

    val tag = ServerCompat::class.simpleName ?: ""

    private var serverSocket: ServerSocket? = null
    private val socketHelpers = Vector<SocketHelper>()

    private var stoppedServer = false

    fun startServer() {
        thread { startServerInside() }
    }

    private fun startServerInside() {
        runCatching {
            val serverSocket = ServerSocket(port).also { this.serverSocket = it }
            runOnUiThread { listener?.onCreateSuccess(this@ServerCompat) }

            while (!serverSocket.isClosed) {
                val socketHelper = SocketHelper(
                    USE_TYPE,
                    serverSocket.accept(),
                    receiveType,
                    charset,
                    ListenerCompat(useType = USE_TYPE, serverListener = listener),
                )
                socketHelpers.add(socketHelper)
            }
        }.onFailure {
            logE(it, tag)
            runOnUiThread { listener?.onCreateFailed(it, this@ServerCompat) }

            if (retryRandomPort) {
                port = Random.nextInt(1024 + 1, 65535)
                startServerInside()
            }
        }
    }

    fun sendDataRawToAll(dataRaw: ByteArray) = socketHelpers.forEach { sendDataRaw(it, dataRaw) }
    fun sendMessageToAll(message: String) = socketHelpers.forEach { sendMessage(it, message) }
    fun sendMessageToAll(message: SocketMessage) = socketHelpers.forEach { sendMessage(it, message) }

    fun sendDataRaw(socketHelper: SocketHelper, dataRaw: ByteArray) = socketHelper.sendDataRaw(dataRaw)
    fun sendMessage(socketHelper: SocketHelper, message: String) = socketHelper.sendMessage(message)
    fun sendMessage(socketHelper: SocketHelper, message: SocketMessage) = socketHelper.sendMessage(message)

    fun sendFile(socketHelper: SocketHelper, filePath: String) = socketHelper.sendFile(File(filePath))
    fun sendFile(socketHelper: SocketHelper, file: File) = socketHelper.sendFile(file)

    fun isRunning(): Boolean = !stoppedServer && serverSocket != null && !serverSocket!!.isClosed

    fun haveAnyConnectedClient(): Boolean = socketHelpers.isNotEmpty()

    fun disconnectAll() {
        socketHelpers.forEach { it.disconnect() }
        socketHelpers.clear()
    }

    fun disconnect(socketHelper: SocketHelper) {
        socketHelpers.find { it == socketHelper }?.let {
            it.disconnect()
            socketHelpers.remove(it)
        }
    }

    fun stopServer() {
        if (stoppedServer) return else stoppedServer = true

        disconnectAll()
        serverSocket.closeSafe()
        runOnUiThread { listener?.onServerStop(this@ServerCompat) }
    }
}
