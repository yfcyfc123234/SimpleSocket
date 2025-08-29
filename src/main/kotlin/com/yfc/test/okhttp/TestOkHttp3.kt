package com.yfc.test.okhttp

import com.yfc.socket.ext.logD
import com.yfc.socket.ext.logE
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy

object TestOkHttp3 {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            "https://speed.cloudflare.com/__down?during=download&bytes=104857600".getSimpleByOkHttp().saveAsFileToDir()
            "https://dldir1.qq.com/qqfile/qq/PCQQ9.7.17/QQ9.7.17.29225.exe".getSimpleByOkHttp().saveAsFileToDir()
            "http://yfcserver.top/test//file/docment/123.docx".getSimpleByOkHttp().saveAsFileToDir()
            "http://yfcserver.top/test//file/eook/pdf/免费的PDF转换神器.pdf".getSimpleByOkHttp().saveAsFileToDir()
            "http://yfcserver.top/test//file/eook/txt/鬼吹灯之龙岭迷窟txt全本精校版.txt".getSimpleByOkHttp().saveAsFileToDir()
            "http://yfcserver.top/test//file/image/gif/6.gif".getSimpleByOkHttp().saveAsFileToDir()
            "http://yfcserver.top/test//file/image/icon/dingdongmaicai.png".getSimpleByOkHttp().saveAsFileToDir()
            "https://github.com/goweii/TestUrl/raw/master/AndroidKeyMd5.pdf".getSimpleByOkHttp().saveAsFileToDir()
        }
    }

    private fun Response.saveAsFileToDir(saveDirPath: String = "C:/Users/Administrator/Desktop/test"): Boolean {
        val saveDir = File(saveDirPath)
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            println("无法创建目录: ${saveDir.absolutePath}")
            return false
        }
        val baseFileName = request.url.pathSegments.lastOrNull() ?: "${System.nanoTime()}"
        val targetFile = getUniqueFileName(saveDir, baseFileName)
        return saveAsFile(targetFile.absolutePath)
    }

    private fun Response.saveAsFile(saveFilePath: String): Boolean {
        val targetFile = File(saveFilePath)
        return runCatching {
            body.byteStream().use { input -> targetFile.outputStream().use { output -> input.copyTo(output) } }
            logE("文件已保存至: ${targetFile.absolutePath}")
            true
        }.onFailure {
            logE("保存文件失败 ${it.message}")
        }.getOrElse { false }
    }

    private fun getUniqueFileName(directory: File, baseFileName: String): File {
        val fileNameWithoutExtension = baseFileName.substringBeforeLast('.', baseFileName)
        val fileExtension = baseFileName.substringAfterLast('.', "").takeIf { it.isNotEmpty() }?.let { ".$it" } ?: ""
        var targetFile = File(directory, baseFileName)
        if (!targetFile.exists()) return targetFile
        var counter = 1
        while (true) {
            val newFileName = "${fileNameWithoutExtension}($counter)$fileExtension"
            targetFile = File(directory, newFileName)
            if (!targetFile.exists()) return targetFile
            counter++
        }
    }

    private fun String.getSimpleByOkHttp(): Response {
        val request: Request = Request.Builder()
            .url(this)
            .headers(
                Headers.headersOf(
//                    "If-Modified-Since", "Fri, 13 May 2022 03:50:50 GMT",
//                    "If-None-Match", "627dd59a-4638",
//                    "Range", "bytes=0-500",
                )
            )
            .get()
            .build()

        val response = OkHttpClient.Builder()
            .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 1089)))
//            .addInterceptor {
//                logD("1")
//                val r = it.proceed(it.request())
//                logD("11")
//                r
//            }
//            .addInterceptor {
//                logD("2")
//                val r = it.proceed(it.request())
//                logD("22")
//                r
//            }
//            .addInterceptor {
//                logD("3")
//                val r = it.proceed(it.request())
//                logD("33")
//                r
//            }
//            .addInterceptor {
//                logD("4")
//                val r = it.proceed(it.request())
//                logD("44")
//                r
//            }
            .addInterceptor(HttpLoggingInterceptor { message -> logD(message) }.apply { level = Level.HEADERS })
            .build()
            .newCall(request)
            .execute()

        return response
    }
}