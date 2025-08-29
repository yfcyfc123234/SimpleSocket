package com.yfc.socket.ext

import com.yfc.socket.ClientCompat
import com.yfc.socket.ServerCompat
import com.yfc.socket.helper.SocketHelper

enum class UseType(val hint: Int, val tag: String) {
    SERVER(100, "server"),
    CLIENT(200, "client"),
}

enum class ReceiveType(val maxLen: Int, val dataType: Int) {
    STRING_READ_LINE(2048, 1),

    STRING_2048(2048, 1),
    STRING_4096(4096, 1),
    STRING_10240(10240, 1),
    STRING_20480(20480, 1),

    BYTE_2048(2048, 2),
    BYTE_4096(4096, 2),
    BYTE_10240(10240, 2),
    BYTE_20480(20480, 2),
}

enum class SocketMessageType(val hint: Int) {
    STRING(100),
    BYTE(200),
    FILE_PATH(300),
}

class SocketMessage(
    val type: SocketMessageType = SocketMessageType.STRING,
    val message: String = "",
    val dataRaw: ByteArray = byteArrayOf(),
    val filePath: String = "",
) {
    override fun toString(): String {
        return "SocketMessage(type=$type, message='$message', dataRaw.size=${dataRaw.size}, filePath='$filePath')"
    }
}

class ListenerCompat(
    var useType: UseType,
    var serverListener: ServerSocketListener? = SimpleServerSocketListener(),
    var clientListener: ClientSocketListener? = SimpleClientSocketListener(),
)

interface ServerSocketListener {
    fun onCreateFailed(throwable: Throwable, server: ServerCompat)
    fun onCreateSuccess(server: ServerCompat)
    fun onClientConnected(helper: SocketHelper)
    fun onClientDisconnected(helper: SocketHelper)
    fun onClientMessageSend(message: SocketMessage, success: Boolean, helper: SocketHelper)
    fun onClientMessageReceived(message: SocketMessage, helper: SocketHelper)
    fun onServerStop(server: ServerCompat)
}

interface ClientSocketListener {
    fun onConnectFailed(throwable: Throwable, client: ClientCompat)
    fun onConnected(helper: SocketHelper)
    fun onDisconnected(helper: SocketHelper)
    fun onMessageSend(message: SocketMessage, success: Boolean, helper: SocketHelper)
    fun onMessageReceived(message: SocketMessage, helper: SocketHelper)
}

open class SimpleServerSocketListener : ServerSocketListener {
    override fun onCreateFailed(throwable: Throwable, server: ServerCompat) {
        logD("onCreateFailed ${server.port}", server.tag)
        logE(throwable, server.tag)
    }

    override fun onCreateSuccess(server: ServerCompat) {
        logD("onCreateSuccess ${server.port}", server.tag)
    }

    override fun onClientConnected(helper: SocketHelper) {
        logD("onClientConnected ${helper.inetAddress}", helper.tag)
    }

    override fun onClientDisconnected(helper: SocketHelper) {
        logD("onClientDisconnected ${helper.inetAddress}", helper.tag)
    }

    override fun onClientMessageSend(message: SocketMessage, success: Boolean, helper: SocketHelper) {
        logD("onClientMessageSend $success message=${message}", helper.tag)
    }

    override fun onClientMessageReceived(message: SocketMessage, helper: SocketHelper) {
        logD("onClientMessageReceived message=${message}", helper.tag)
    }

    override fun onServerStop(server: ServerCompat) {
        logD("onServerStop", server.tag)
    }
}

open class SimpleClientSocketListener : ClientSocketListener {
    override fun onConnectFailed(throwable: Throwable, client: ClientCompat) {
        logD("onConnectFailed ${client.host}:${client.port}", client.tag)
        logE(throwable, client.tag)
    }

    override fun onConnected(helper: SocketHelper) {
        logD("onConnected ${helper.inetAddress}", helper.tag)
    }

    override fun onDisconnected(helper: SocketHelper) {
        logD("onDisconnected ${helper.inetAddress}", helper.tag)
    }

    override fun onMessageSend(message: SocketMessage, success: Boolean, helper: SocketHelper) {
        logD("onMessageSend $success message=${message}", helper.tag)
    }

    override fun onMessageReceived(message: SocketMessage, helper: SocketHelper) {
        logD("onMessageReceived message=${message}", helper.tag)
    }
}