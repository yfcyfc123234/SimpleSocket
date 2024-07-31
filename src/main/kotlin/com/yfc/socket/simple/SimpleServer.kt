package com.yfc.com.yfc.socket.simple

import com.yfc.com.yfc.socket.ext.*
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

class SimpleServer(private val port: Int, val lineRead: Boolean = true) {
    val tag = SimpleServer::class.simpleName ?: ""

    private var serverSocket: ServerSocket? = null
    private val clientHandlers = Vector<ClientHandler>()

    var onCreateSuccess: ((port: Int) -> Unit)? = null
    var onCreateFailed: (() -> Boolean)? = null
    var onClientConnected: ((Socket) -> Unit)? = null
    var onClientDisconnected: ((Socket) -> Unit)? = null
    var onMessageReceived: ((Socket, String) -> Unit)? = null

    fun startServer() {
        thread {
            runCatching {
                val serverSocket = ServerSocket(port).also { this.serverSocket = it }

                logE("Server started on port: $port", tag)
                onCreateSuccess?.invoke(port)

                while (!serverSocket.isClosed) {
                    runCatching {
                        val clientSocket = serverSocket.accept()

                        logE("onClientConnected clientSocket=${clientSocket}", tag)

                        val handler = ClientHandler(clientSocket).apply { start() }
                        clientHandlers.add(handler)

                        runOnUiThread { onClientConnected?.invoke(clientSocket) }
                    }.onFailure {
                        logE(it, tag)
                    }
                }
            }.onFailure {
                logE(it, tag)
                runOnUiThread { onCreateFailed?.invoke() }
            }
        }
    }

    fun sendMessage(clientSocket: Socket, message: String) {
        val handler = clientHandlers.find { it.clientSocket == clientSocket }
        handler?.sendMessage(message)
    }

    fun stopServer() {
        clientHandlers.forEach { it.disconnect() }

        serverSocket.closeSafe()
    }

    fun isRunning(): Boolean = serverSocket?.isClosed != true

    fun sendMessageToAll(message: String) {
        clientHandlers.forEach { it.sendMessage(message) }
    }

    fun haveAnyConnectedClient(): Boolean = clientHandlers.isNotEmpty()

    fun disconnectBySocket(clientSocket: Socket) {
        val handler = clientHandlers.find { it.clientSocket == clientSocket }
        handler?.disconnect()
    }

    inner class ClientHandler(val clientSocket: Socket) : Thread() {
        private var output: OutputStream? = clientSocket.getOutputStream()
        private var input: InputStream? = clientSocket.getInputStream()

        override fun run() {
            super.run()

            isDisconnect = false
            listen()
        }

        private fun listen() {
            try {
                while (isConnected() && !isDisconnect && input != null) {
                    var l: Int
                    val b = ByteArray(2048)
                    while (input!!.read(b, 0, b.size).also { l = it } != -1) {
                        val message = String(b, 0, l)
                        logE("message=${message}", tag)
                        if (message.isNotEmpty()) {
                            runOnUiThread { onMessageReceived?.invoke(clientSocket, message) }
                        }
                    }
                }
            } catch (e: Exception) {
                logE(e, tag)
            } finally {
                disconnect()
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

                    logE("sendMessage message=${message}", tag)
                }
            }.onFailure {
                logE(it, tag)
            }
        }

        private var isDisconnect = false

        fun disconnect() {
            if (isDisconnect) return
            isDisconnect = true

            onCreateFailed = null
            onClientConnected = null
            onMessageReceived = null

            clientSocket.closeSafe()
            output.closeSafe()
            input.closeSafe()

            clientHandlers.remove(this)

            runOnUiThread {
                onClientDisconnected?.invoke(clientSocket)
                onClientDisconnected = null
            }
        }

        fun isConnected(): Boolean = clientSocket.isConnected
    }
}
