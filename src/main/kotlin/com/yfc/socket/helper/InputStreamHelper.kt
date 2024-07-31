package com.yfc.com.yfc.socket.helper

import com.yfc.com.yfc.socket.ext.*
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

class InputStreamHelper(private val socketHelper: SocketHelper) {
    private val tag = socketHelper.tag
    private val charset get() = socketHelper.charset
    private val isDisconnect get() = socketHelper.isDisconnect
    private val socket = socketHelper.socket
    private val input = socket.getInputStream()
    private val reader = BufferedReader(InputStreamReader(input))

    init {
        thread { listen() }
    }

    private fun listen() {
        try {
            while (socket.isConnected && !isDisconnect && input != null) {
                if (socketHelper.receiveType == ReceiveType.STRING_READ_LINE) {
                    var readLine: String
                    while (!reader.readLine().also { readLine = it }.isNullOrEmpty()) {
                        socketHelper.onMessageReceived(SocketMessage(type = SocketMessageType.STRING, message = readLine))
                    }
                } else {
                    val needString = socketHelper.receiveType.dataType == 1

                    var len: Int
                    val data = ByteArray(socketHelper.receiveType.maxLen)
                    while (input.read(data, 0, data.size).also { len = it } != -1) {
                        if (needString) {
                            val message = String(data, 0, len, charset)
                            if (message.isNotEmpty()) {
                                socketHelper.onMessageReceived(SocketMessage(type = SocketMessageType.STRING, message = message))
                            }
                        } else {
                            val dataRaw = ByteArray(len)
                            data.copyInto(dataRaw, 0, 0, len)
                            socketHelper.onMessageReceived(SocketMessage(type = SocketMessageType.STRING, dataRaw = dataRaw))
                        }
                    }
                }
            }
        } catch (throwable: Throwable) {
            logE(throwable, tag)
        } finally {
            socketHelper.disconnect()
        }
    }

    fun close() {
        input.closeSafe()
    }
}