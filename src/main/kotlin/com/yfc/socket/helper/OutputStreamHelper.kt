package com.yfc.com.yfc.socket.helper

import com.yfc.com.yfc.socket.ext.*
import java.io.File

class OutputStreamHelper(private val socketHelper: SocketHelper) {
    private val tag = socketHelper.tag
    private val charset get() = socketHelper.charset
    private val isDisconnect get() = socketHelper.isDisconnect
    private val socket = socketHelper.socket
    private val output = socket.getOutputStream()

    fun sendFile(filePath: String) = sendFile(File(filePath))
    fun sendFile(file: File) {
        if (!file.exists() || file.isDirectory) return
        sendMessage(SocketMessage(type = SocketMessageType.FILE_PATH, filePath = file.absolutePath))
    }

    fun sendDataRaw(dataRaw: ByteArray) = sendMessage(SocketMessage(type = SocketMessageType.BYTE, dataRaw = dataRaw))
    fun sendMessage(message: String) = sendMessage(SocketMessage(type = SocketMessageType.STRING, message = message))
    fun sendMessage(message: SocketMessage) {
        if (isMainThread()) {
            getCachedPool().submit { sendMessageInside(message) }
        } else {
            sendMessageInside(message)
        }
    }

    private fun sendMessageInside(message: SocketMessage) {
        val success = when (message.type) {
            SocketMessageType.STRING -> sendData(message.message.toByteArray(charset))
            SocketMessageType.BYTE -> sendData(message.dataRaw)
            SocketMessageType.FILE_PATH -> false // TODO yfc
        }

        runOnUiThread {
            when (socketHelper.useType) {
                UseType.SERVER -> socketHelper.listener?.serverListener?.onClientMessageSend(message, success, socketHelper)
                UseType.CLIENT -> socketHelper.listener?.clientListener?.onMessageSend(message, success, socketHelper)
            }
        }
    }

    private fun sendData(data: ByteArray): Boolean {
        return runCatching {
            val result = if (socketHelper.receiveType == ReceiveType.STRING_READ_LINE) {
                val newline = "\n".toByteArray(charset)

                val addNewline = if (data.size < newline.size) {
                    true
                } else {
                    !data.copyOfRange(data.size - newline.size, data.size).contentEquals(newline)
                }

                if (addNewline) {
                    ByteArray(data.size + newline.size).apply {
                        data.copyInto(this, 0)
                        newline.copyInto(this, this.size - newline.size)
                    }
                } else {
                    data
                }
            } else {
                data
            }

            output?.apply {
                write(result)
                flush()
            }

            true
        }.onFailure {
            logE(it, tag)
        }.getOrNull() ?: false
    }

    fun close() {
        output.closeSafe()
    }
}