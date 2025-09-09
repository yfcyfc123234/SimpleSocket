package com.yfc.com.yfc.docx.poi

import com.yfc.docx.toOrNull
import com.yfc.socket.ext.logE
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Method

object DocxEditor {
    private val TAG = DocxEditor::class.simpleName ?: ""

    private fun log(log: String) = logE(log, TAG)

    fun editDocx(
        input: File,
        output: File,
        docxEditBean: DocxEditBean,
    ) {
        val editBean = docxEditBean.copyNewValid()

        input.inputStream().use { inputStream ->
            XWPFDocument(inputStream).apply {
                val pList = mutableListOf<XWPFParagraph>()

                pList.addAll(paragraphs.toMutableList())

                tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            pList.addAll(cell.paragraphs.toMutableList())
                        }
                    }
                }

                val aaa = pList.filter { it.text.isNullOrEmpty() }
                val ctp = aaa.firstOrNull()?.ctp
                val n = ctp?.domNode?.toOrNull<Element>()?.let {
                    val n = it.nodeName
                    val v = it.nodeValue
                    val r = it.getAttributeNode("r")
                    val c = it.childNodes
                    val a = it.firstChild
                    val aaa = it.getElementsByTagName("*")
                    val aaaaaa = (0 until aaa.length).map { m -> aaa.item(m) }
                    val ddddd = aaaaaa.filter { f -> f.nodeName == "w:t" }
                    ddddd.lastOrNull()?.let { l ->
                        val e =  (l as Element)
                        e.textContent
                     val n =    (l as Element).nodeValue
                        logE("it.nodeValue=${l.nodeValue}")
                    }
                    logE()
                }

//                val ctr = aaa.firstOrNull()?.runs?.firstOrNull()?.ctr
//                ctr?.annotationRefArray
//                val list = pList.map { (it.paragraphText ?: "") to it }.toMutableList()
//
//                log(list.filter { it.first.isNotEmpty() }.joinToString("\n") { it.first })
//
//                replaceDocx(list, editBean.replaces)
//                showDocx(list, editBean.shows)
//
//                replaceUserAvatarPlaceholder(this, "asdasdqweqwewqeq")
//
//                output.outputStream().use { outputStream -> write(outputStream) }

                close()
            }
        }
    }

    /**
     * 安全调用对象中所有以"List"结尾的无参方法（避免XML相关类依赖问题）
     * @param obj 要调用方法的对象
     * @return 包含所有符合条件方法返回结果的集合
     */
    fun safeInvokeAllListMethods(obj: Any): List<Any?> {
        val resultList = mutableListOf<Any?>()

        try {
            // 获取类自身声明的所有方法（不包括继承的，减少依赖加载）
            val clazz = obj::class.java
            val methods = clazz.declaredMethods  // 优先用自身声明的方法，避免加载父类的XML相关方法

            // 筛选条件：以"List"结尾、无参数、且不依赖XML相关类
            val validMethods = methods.filter { method ->
                method.name.contains("get")
                        && method.parameterCount == 0
                        && isMethodSafe(method)  // 额外检查方法是否安全（不涉及XML相关类）
            }

            // 调用筛选后的方法
            for (method in validMethods) {
                try {
                    method.isAccessible = true  // 允许访问私有方法
                    val result = method.invoke(obj)
                    resultList.add(method.name to result)
                } catch (e: Exception) {
                    println("调用方法 ${method.name} 失败: ${e.message}")
                    resultList.add(null)
                }
            }
        } catch (e: NoClassDefFoundError) {
            // 捕获XML相关类缺失的错误
            println("警告：检测到缺失依赖类（可能是XML相关），已跳过有问题的方法")
            e.printStackTrace()
        } catch (e: Exception) {
            println("反射过程发生异常: ${e.message}")
            e.printStackTrace()
        }

        return resultList
    }

    /**
     * 检查方法是否安全（不涉及XMLStreamReader等缺失的类）
     */
    private fun isMethodSafe(method: Method): Boolean {
        return true
//        return try {
//            // 检查返回类型是否会触发XML类加载
//            val returnType = method.returnType.name
//            // 过滤掉可能涉及XML的返回类型（根据实际情况调整）
//            !returnType.contains("javax.xml")
//                    && !returnType.contains("org.w3c.dom")
//                    && !returnType.contains("org.xml.sax")
//        } catch (e: Exception) {
//            // 无法检查时默认视为不安全
//            false
//        }
    }

    private fun showDocx(list: MutableList<Pair<String, XWPFParagraph>>, shows: MutableList<DocxShowBean>) {
        showDocxInside(list, shows)
    }

    private fun showDocxInside(list: MutableList<Pair<String, XWPFParagraph>>, shows: MutableList<DocxShowBean>) {
        if (list.isEmpty() || shows.isEmpty()) return

        var needHandleNext = false

        var beginIndex = -1
        var endIndex = -1
        var showBeginEndIndex = -1

        run handleShow@{
            list.forEachIndexed { index, pair ->
                if (beginIndex == -1) {
                    val showIndex = shows.indexOfFirst { pair.first.contains(it.showBegin) }
                    if (showIndex >= 0) {
                        beginIndex = index
                        showBeginEndIndex = showIndex
                    }
                } else {
                    val showIndex = shows.indexOfFirst { pair.first.contains(it.showEnd) }
                    if (showIndex == showBeginEndIndex) {
                        endIndex = index
                    }
                }

                if (beginIndex != -1 && endIndex != -1 && showBeginEndIndex != -1) {
                    val showBean = shows.removeAt(showBeginEndIndex)
                    log("showBean=${showBean}")

                    if (showBean.show) {
                        list.apply {
                            removeAt(endIndex).apply { second.removeAllRuns().removeFromParent(false) }
                            removeAt(beginIndex).apply { second.removeAllRuns().removeFromParent(false) }
                        }
                    } else {
                        (beginIndex..endIndex).reversed().forEach {
                            list.removeAt(it).apply { second.removeAllRuns().removeFromParent(false) }
                        }
                    }

                    needHandleNext = true
                    return@handleShow
                }
            }
        }

        if (needHandleNext) {
            showDocxInside(list, shows)
        } else {
            if (showBeginEndIndex != -1) {
                shows.removeAt(showBeginEndIndex)
                showDocxInside(list, shows)
            }
        }
    }

    private fun replaceDocx(list: MutableList<Pair<String, XWPFParagraph>>, replaces: MutableList<DocxReplaceBean>) {
        replaces.forEach { replace ->
            run r@{
                list.forEach {
                    if (it.second.replaceInParagraph(replace)) return@r
                }
            }
        }
    }

    private fun XWPFParagraph.replaceInParagraph(replace: DocxReplaceBean): Boolean {
        val paragraphText = paragraphText ?: ""
        val runs = runs

        if (paragraphText.isNotEmpty() && !runs.isNullOrEmpty()) {
            if (paragraphText.contains(replace.template)) {
                val template = replace.template
                val text = replace.text

                log("paragraphText=${paragraphText} runs=${runs.joinToString("、")} replace=${replace}")

                val preciselyRun = runs.find { it.text().contains(template) }
                if (preciselyRun != null) {
                    preciselyRun.setText(preciselyRun.text().replace(template, text))
                } else {
                    var handled = false
                    val runTexts = runs.map { it.text() }.toMutableList()

                    run handle@{
                        runTexts.forEachIndexed { fromIndex, string ->
                            (fromIndex + 1..runTexts.size).forEach { toIndex ->
                                val textOld = runTexts.subList(fromIndex, toIndex).joinToString("")
                                if (textOld == template) {
                                    var setDone = false
                                    (fromIndex until toIndex).forEach { i ->
                                        val run = runs.getOrNull(i)
                                        if (!setDone) {
                                            setDone = true
                                            run?.replaceText(text)
                                        } else {
                                            run?.replaceText("")
                                        }
                                    }
                                    handled = true
                                    return@handle
                                }
                            }
                        }
                    }

                    if (!handled) setJustOneText(text)
                }

                return true
            }
        }

        return false
    }

    private fun XWPFParagraph.setJustOneText(text: String): XWPFParagraph {
        runs.firstOrNull()?.replaceText(text)
        (1 until runs.size).reversed().forEach { removeRun(it) }
        return this
    }

    private fun XWPFParagraph.removeAllRuns(): XWPFParagraph {
        (0 until runs.size).reversed().forEach { removeRun(it) }
        return this
    }

    private fun XWPFRun.replaceText(text: String): XWPFRun {
        setText(text, 0)
        return this
    }

    private fun XWPFRun.removeFromParent(): XWPFRun {
        (parent as? XWPFParagraph)?.apply {
            val index = runs.indexOfFirst { it == this@removeFromParent }
            if (index >= 0) removeRun(index)
        }
        return this
    }

    fun XWPFParagraph.removeFromParent(childMustNull: Boolean = true): XWPFParagraph {
        if (childMustNull && runs.isNotEmpty()) return this
        val b = body
        if (b is XWPFTableCell) {
            b.apply {
                val index = paragraphs.indexOfFirst { it == this@removeFromParent }
                if (index != -1) removeParagraph(index)
                removeFromParent()
            }
        } else if (b is XWPFDocument) {
            b.apply {
                val index = bodyElements.indexOfFirst { it == this@removeFromParent }
                if (index != -1) removeBodyElement(index)
            }
        }
        return this
    }

    fun XWPFTableCell.removeFromParent(childMustNull: Boolean = true): XWPFTableCell {
        if (childMustNull && paragraphs.isNotEmpty()) return this
        tableRow.apply {
            val index = tableCells.indexOfFirst { it == this@removeFromParent }
            if (index != -1) removeCell(index)
            removeFromParent()
        }
        return this
    }

    fun XWPFTableRow.removeFromParent(childMustNull: Boolean = true): XWPFTableRow {
        if (childMustNull && tableCells.isNotEmpty()) return this
        table.apply {
            val index = rows.indexOfFirst { it == this@removeFromParent }
            if (index != -1) removeRow(index)
            removeFromParent()
        }
        return this
    }

    fun XWPFTable.removeFromParent(childMustNull: Boolean = true): XWPFTable {
        if (childMustNull && rows.isNotEmpty()) return this
        (body as? XWPFDocument)?.apply {
            val index = bodyElements.indexOfFirst { it == this@removeFromParent }
            if (index != -1) removeBodyElement(index)
        }
        return this
    }

    ///////////////

    /**
     * 替换 Word 文档中所有文本框内的 ${userAvatar} 占位符
     * @param doc 目标 XWPF 文档
     * @param replacement 替换后的文本
     */
    fun replaceUserAvatarPlaceholder(doc: XWPFDocument, replacement: String) {
        // 遍历文档中所有段落
        doc.paragraphs.forEach { paragraph ->
            // 处理段落中包含的文本框内容
            processTextBoxInParagraph(paragraph, replacement)
        }
    }

    /**
     * 处理单个段落中的文本框内容，替换占位符
     */
    private fun processTextBoxInParagraph(paragraph: XWPFParagraph, replacement: String) {
        // 获取段落的底层 XML 元素（CTP）
        val ctp: CTP = paragraph.ctp
        val paragraphNode = ctp.domNode

        // 递归查找段落中所有 <w:txbxContent> 节点（文本框内容容器）
        val textboxContents = findNodesByTagName(paragraphNode, "w:txbxContent")
        textboxContents.forEach { textboxNode ->
            // 从 <w:txbxContent> 中提取所有 <w:p> 段落节点
            val pNodes = findNodesByTagName(textboxNode, "w:p")
            pNodes.forEach { pNode ->
                // 遍历段落中的 <w:r> 文本 run
                val rNodes = findNodesByTagName(pNode, "w:r")
                rNodes.forEach { rNode ->
                    // 查找文本 run 中的 <w:t> 文本节点
                    val tNodes = findNodesByTagName(rNode, "w:t")
                    tNodes.forEach { tNode ->
                        // 替换占位符
                        val nodeValue = tNode.nodeValue ?: ""
                        if (nodeValue.contains($$"${userAvatar}")) {
                            tNode.nodeValue = nodeValue.replace($$"${userAvatar}", replacement)
                        }
                    }
                }
            }
        }
    }

    /**
     * 递归查找指定标签名的所有节点（支持命名空间前缀，如 "w:txbxContent"）
     */
    private fun findNodesByTagName(parent: Node, tagName: String): List<Node> {
        val result = mutableListOf<Node>()
        val nodeList = parent.childNodes
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            // 匹配标签名（包含前缀，如 "w:txbxContent"）
            if (node.nodeName == tagName) {
                result.add(node)
            }
            // 递归查找子节点
            result.addAll(findNodesByTagName(node, tagName))
        }
        return result
    }

    // 使用示例
    fun main() {
        val inputPath = "input.docx"  // 输入文档路径
        val outputPath = "output.docx"  // 输出文档路径
        val replacementText = "用户头像替换文本"  // 替换后的文本

        // 读取文档
        val fis = FileInputStream(inputPath)
        val doc = XWPFDocument(fis)

        // 替换占位符
        replaceUserAvatarPlaceholder(doc, replacementText)

        // 保存修改后的文档
        val fos = FileOutputStream(outputPath)
        doc.write(fos)

        // 关闭资源
        fos.close()
        doc.close()
        fis.close()

        println("占位符替换完成，输出文件：$outputPath")
    }
}