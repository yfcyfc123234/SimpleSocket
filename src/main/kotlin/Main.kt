package com.yfc

import com.yfc.socket.ClientCompat
import com.yfc.socket.ServerCompat
import com.yfc.socket.ext.ReceiveType
import com.yfc.socket.ext.SimpleClientSocketListener
import com.yfc.socket.ext.SimpleServerSocketListener
import com.yfc.socket.helper.SocketHelper
import com.yfc.socket.simple.SimpleClient
import com.yfc.socket.simple.SimpleServer
import java.util.concurrent.TimeUnit

fun main() {
    testSimple()
    TimeUnit.SECONDS.sleep(1)
    testCompat()
}

fun testSimple() {
    SimpleServer(3215).apply {
        onCreateSuccess = { port ->
            startSimpleClient(port)
        }
        startServer()
    }
}

fun startSimpleClient(port: Int, host: String = "127.0.0.1") {
    SimpleClient(host, port).apply {
        onConnected = { sendMessage("testSimpleClient") }
        connect()
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
fun testCompat() {
    ServerCompat(
        port = 4563,
        receiveType = ReceiveType.STRING_2048,
        charset = Charsets.UTF_8,
        listener = object : SimpleServerSocketListener() {
            override fun onCreateSuccess(server: ServerCompat) {
                super.onCreateSuccess(server)
                startClientCompat(server)
            }
        },
    ).startServer()
}

private fun startClientCompat(server: ServerCompat, host: String = "127.0.0.1") {
    ClientCompat(
        host = host,
        port = server.port,
        receiveType = server.receiveType,
        charset = server.charset,
        listener = object : SimpleClientSocketListener() {
            override fun onConnected(helper: SocketHelper) {
                super.onConnected(helper)
                helper.sendMessage("testClientCompat")
            }
        },
    ).connect()
}
