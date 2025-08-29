package com.yfc.docx

import com.yfc.socket.ext.logE
import org.jodconverter.core.DocumentConverter
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import org.jodconverter.core.office.OfficeException
import org.jodconverter.local.LocalConverter
import org.jodconverter.local.office.LocalOfficeManager
import java.io.Closeable
import java.io.File
import java.io.IOException

class OfficeToPdfConverter : Closeable {
    // 配置Office管理器
    private val officeManager by lazy {
        LocalOfficeManager
            .builder()
            .install()
            .officeHome("D:/Program Files/LibreOffice/")
            .portNumbers(8100) // 与soffice监听端口一致
            .processTimeout(120000L) // 连接超时（毫秒）
            .build()
    }

    private var converter: DocumentConverter? = null

    /**
     * 初始化转换器
     */
    fun start() {
        officeManager.start()
        converter = LocalConverter
            .builder()
            .officeManager(officeManager)
            .build()
    }

    /**
     * 将Office文档转换为PDF
     *
     * @param inputFile  输入文件（.docx, .xlsx, .pptx等）
     * @param outputFile 输出PDF文件
     * @throws OfficeException 转换异常
     * @throws IOException     IO异常
     */
    fun convertToPdf(inputFile: File, outputFile: File) {
        if (!inputFile.exists()) throw IOException("输入文件不存在: " + inputFile.absolutePath)

        outputFile.getParentFile().mkdirs()

        try {
            converter!!.convert(inputFile)
                .to(outputFile)
                .`as`(DefaultDocumentFormatRegistry.PDF)
                .execute()

            logE("✅ 转换成功: " + inputFile.getName() + " -> " + outputFile.getName())
        } catch (e: OfficeException) {
            logE("❌ 转换失败: " + e.message)
            throw e
        }
    }

    /**
     * 关闭资源
     */
    fun shutdown() {
        runCatching {
            officeManager.stop()
        }.onFailure {
            logE(it)
        }
    }

    override fun close() {
        shutdown()
    }
}