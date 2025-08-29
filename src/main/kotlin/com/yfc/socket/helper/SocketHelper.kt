package com.yfc.socket.helper

import com.yfc.socket.ext.ListenerCompat
import com.yfc.socket.ext.ReceiveType
import com.yfc.socket.ext.SocketMessage
import com.yfc.socket.ext.UseType
import com.yfc.socket.ext.closeSafe
import com.yfc.socket.ext.runOnUiThread
import java.io.File
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class SocketHelper(
    val useType: UseType,
    val socket: Socket,
    var receiveType: ReceiveType = ReceiveType.STRING_2048,
    var charset: Charset = Charsets.UTF_8,
    var listener: ListenerCompat? = null,
) {
    @Volatile
    var isDisconnect = false

    val tag: String get() = useType.tag

    val inetAddress = socket.inetAddress
    val inputStreamHelper = InputStreamHelper(this)
    val outputStreamHelper = OutputStreamHelper(this)

    init {
        // Wait for inputStreamHelper listen to be ready
        // TODO yfc Can judge more accurately
        TimeUnit.MILLISECONDS.sleep(500)

        runOnUiThread {
            when (useType) {
                UseType.SERVER -> listener?.serverListener?.onClientConnected(this)
                UseType.CLIENT -> listener?.clientListener?.onConnected(this)
            }
        }
    }

    fun disconnect() {
        if (isDisconnect) return else isDisconnect = true

        socket.closeSafe()
        inputStreamHelper.close()
        outputStreamHelper.close()

        runOnUiThread {
            when (useType) {
                UseType.SERVER -> listener?.serverListener?.onClientDisconnected(this)
                UseType.CLIENT -> listener?.clientListener?.onDisconnected(this)
            }
            listener = null
        }
    }

    fun isConnected(): Boolean = !isDisconnect && socket.isConnected

    fun sendFile(filePath: String) = outputStreamHelper.sendFile(File(filePath))
    fun sendFile(file: File) = outputStreamHelper.sendFile(file)

    fun sendDataRaw(dataRaw: ByteArray) = outputStreamHelper.sendDataRaw(dataRaw)
    fun sendMessage(message: String) = outputStreamHelper.sendMessage(message)
    fun sendMessage(message: SocketMessage) = outputStreamHelper.sendMessage(message)

    fun onMessageReceived(message: SocketMessage) {
        runOnUiThread {
            when (useType) {
                UseType.SERVER -> listener?.serverListener?.onClientMessageReceived(message, this)
                UseType.CLIENT -> listener?.clientListener?.onMessageReceived(message, this)
            }
        }
    }
}