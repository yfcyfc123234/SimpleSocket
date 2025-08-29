package com.yfc.docx

import com.yfc.socket.ext.logD
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions
import jakarta.xml.bind.JAXBElement
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.docx4j.Docx4J
import org.docx4j.XmlUtils
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingGroup.CTWordprocessingGroup
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape
import org.docx4j.dml.CTBlip
import org.docx4j.dml.CTBlipFillProperties
import org.docx4j.dml.CTRelativeRect
import org.docx4j.dml.CTStretchInfoProperties
import org.docx4j.dml.wordprocessingDrawing.Anchor
import org.docx4j.fonts.IdentityPlusMapper
import org.docx4j.fonts.PhysicalFonts
import org.docx4j.mce.AlternateContent
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage
import org.docx4j.vml.CTFill
import org.docx4j.vml.CTRect
import org.docx4j.vml.CTShape
import org.docx4j.vml.CTTextbox
import org.docx4j.wml.*
import org.jvnet.jaxb2_commons.ppp.Child
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

object DocxUtil {
    //    private val factory by lazy { Context.getWmlObjectFactory() }
    private lateinit var docxBean: DocxBean
    private val imageFilePath by lazy { "C:/Users/Administrator/Pictures/ComfyUI_00476_.png" }

    @JvmStatic
    fun main(args: Array<String>) {
        val docxInput = "C:/Users/Administrator/Desktop/resume_tpl1.docx"
        val docxOutput = "C:/Users/Administrator/Desktop/resume_tpl1_out.docx"
        val pdfOutputByJodConverter = "C:/Users/Administrator/Desktop/wordToPdfByJodConverter.pdf"
        val pdfOutputByPoi = "C:/Users/Administrator/Desktop/wordToPdfByPoi.pdf"
        val pdfOutputByDocx4J = "C:/Users/Administrator/Desktop/wordToPdfByDocx4j.pdf"

        handleDocx(DocxBean.createTest(), docxInput, docxOutput)

        wordToPdfByJodConverter(docxOutput, pdfOutputByJodConverter)
//        wordToPdfByPoi(docxOutput, pdfOutputByPoi)
//        wordToPdfByDocx4J(docxInput, pdfOutputByDocx4J)
    }

    private fun wordToPdfByJodConverter(input: String, output: String) {
        OfficeToPdfConverter().apply {
            start()
            use { convertToPdf(File(input), File(output)) }
        }
    }

    private fun wordToPdfByPoi(input: String, output: String) {
        val document = XWPFDocument(FileInputStream(input)).apply { createNumbering() }
        FileOutputStream(output).use {
            PdfConverter.getInstance().convert(document, it, PdfOptions.create())
        }
    }

    private fun wordToPdfByDocx4J(input: String, output: String): String {
        val (wordMLPackage, openTime) = measureTimedValue { WordprocessingMLPackage.load(File(input)) }

        logD("openTime=${openTime.toLong(DurationUnit.MILLISECONDS)}")

        wordMLPackage.fontMapper = IdentityPlusMapper().apply {
            put("隶书", PhysicalFonts.get("LiSu"))
            put("宋体", PhysicalFonts.get("SimSun"))
            put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"))
            put("黑体", PhysicalFonts.get("SimHei"))
            put("楷体", PhysicalFonts.get("KaiTi"))
            put("新宋体", PhysicalFonts.get("NSimSun"))
            put("华文行楷", PhysicalFonts.get("STXingkai"))
            put("华文仿宋", PhysicalFonts.get("STFangsong"))
            put("仿宋", PhysicalFonts.get("FangSong"))
            put("幼圆", PhysicalFonts.get("YouYuan"))
            put("华文宋体", PhysicalFonts.get("STSong"))
            put("华文中宋", PhysicalFonts.get("STZhongsong"))
            put("等线", PhysicalFonts.get("SimSun"))
            put("等线 Light", PhysicalFonts.get("SimSun"))
            put("华文琥珀", PhysicalFonts.get("STHupo"))
            put("华文隶书", PhysicalFonts.get("STLiti"))
            put("华文新魏", PhysicalFonts.get("STXinwei"))
            put("华文彩云", PhysicalFonts.get("STCaiyun"))
            put("方正姚体", PhysicalFonts.get("FZYaoti"))
            put("方正舒体", PhysicalFonts.get("FZShuTi"))
            put("华文细黑", PhysicalFonts.get("STXihei"))
            put("宋体扩展", PhysicalFonts.get("simsun-extB"))
            put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"))
        }

        // html
//            val output2 = "C:/Users/Administrator/Desktop/resume_tpl1_out.html"
//            Docx4J.toHTML(HTMLSettings().apply { opcPackage = wordMLPackage }, FileOutputStream(File(output2)), 0)

//                Docx4J.toFO(
//                    FOSettings().apply { opcPackage = wordMLPackage },
//                    FileOutputStream(File(output3)),
////            Docx4J.FLAG_EXPORT_PREFER_NONXSL,
//                    0,
//                )
        Docx4J.toPDF(wordMLPackage, FileOutputStream(File(output)))

        return output
    }

    fun handleDocx(docxBean: DocxBean, input: String, output: String): String {
        DocxUtil.docxBean = docxBean

        val (wordMLPackage, openTime) = measureTimedValue { WordprocessingMLPackage.load(File(input)) }

        logD("openTime=${openTime.toLong(DurationUnit.MILLISECONDS)}")

        val handleTime = measureTime {
            handleImage(wordMLPackage)
            wordMLPackage.mainDocumentPart.contents.apply {
                handleAlternateContent(this)
                handleChildren(this)
            }
        }
        logD("handleTime=$handleTime")

        val saveTime = measureTime {
            //docx
            Docx4J.save(wordMLPackage, File(output))
//                SaveToZipFile(wordMLPackage).save(output)
        }
        logD("saveTime=$saveTime")

        return output
    }

    fun handleImage(wordMLPackage: WordprocessingMLPackage) {
        val imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, File(imageFilePath))
        val acList = docxBean.ac?.filter { it.type == DocxACBean.TYPE_IMAGE } ?: mutableListOf()
        val alternateContentList = wordMLPackage.mainDocumentPart
            .descendants
            .filter { it.content is AlternateContent }
            .map { it.content.toOrNull<AlternateContent>() }
            .toMutableList()
        acList.forEach { ac ->
            alternateContentList.forEach {
                it?.apply {
                    val ctWordprocessing = choice?.getOrNull(0)
                        ?.any
                        ?.getOrNull(0)
                        ?.toOrNull<Drawing>()
                        ?.anchorOrInline
                        ?.getOrNull(0)
                        ?.toOrNull<Anchor>()
                        ?.graphic
                        ?.graphicData
                        ?.any
                        ?.getOrNull(0)
                        ?.toOrNull<JAXBElement<*>>()
                        ?.value

                    when (ctWordprocessing) {
                        is CTWordprocessingShape -> {
                            findText(ctWordprocessing) { _, text ->
                                if (text?.value == ac.replace) {
                                    setRelId(ctWordprocessing, imagePart.relLast.id)
                                }
                            }
                        }

                        is CTWordprocessingGroup -> {
                            ctWordprocessing.toOrNull<CTWordprocessingGroup>()
                                ?.wspOrGrpSpOrGraphicFrame
                                ?.forEach { f ->
                                    findText(f) { _, text ->
                                        if (text?.value == ac.replace) {
                                            setRelId(f.toOrNull<CTWordprocessingShape>(), imagePart.relLast.id)
                                        }
                                    }
                                }
                        }

                        else -> {
                            // nothing
                        }
                    }

                    val elementList1 = fallback
                        ?.any
                        ?.getOrNull(0)
                        ?.toOrNull<Pict>()
                        ?.anyAndAny
                        ?.getOrNull(0)
                        ?.toOrNull<CTShape>()
                        ?.egShapeElements
                        ?.filter { f -> f is JAXBElement }
                        ?.map { m -> m.to<JAXBElement<*>>() }

                    val elementList2 = fallback
                        ?.any
                        ?.getOrNull(0)
                        ?.toOrNull<Pict>()
                        ?.anyAndAny
                        ?.getOrNull(0)
                        ?.toOrNull<JAXBElement<*>>()
                        ?.value
                        ?.toOrNull<CTRect>()
                        ?.egShapeElements
                        ?.filter { f -> f is JAXBElement }
                        ?.map { m -> m.to<JAXBElement<*>>() }

                    val elementList = (elementList1 ?: elementList2) ?: return

                    val ctTextbox = elementList.find { f -> f.value is CTTextbox }?.value.toOrNull<CTTextbox>()
                    if (ctTextbox != null) {
                        if (ctTextbox.txbxContent.content.find { f -> f.toOrNull<P>()?.getText() == ac.replace } != null) {
                            elementList.find { f -> f.value is CTFill }?.value.toOrNull<CTFill>()?.id = imagePart.relLast.id
                        }
                    }
                }
            }
        }
    }

    fun setRelId(shape: CTWordprocessingShape?, id: String) {
        shape?.spPr?.apply {
            noFill = null
            blipFill = CTBlipFillProperties().apply {
                blip = CTBlip().apply { embed = id }
                stretch = CTStretchInfoProperties().apply { fillRect = CTRelativeRect() }
            }
        }
    }

    fun handleAlternateContent(document: Document) {
        document.descendants
            .toMutableList()
            .filter { it.content.toOrNull<P>()?.getText()?.isEmpty() == true }
            .map { it.descendants.toMutableList() }
            .filter { !it.isEmpty() }
            .flatMap { it }
            .filter { it.content is AlternateContent }
            .map { it.content.toOrNull<AlternateContent>() }
            .forEach {
                it?.choice?.forEach { c ->
                    c.any.forEach { any ->
                        any.toOrNull<Drawing>()
                            ?.anchorOrInline
                            ?.forEach { an ->
                                an.toOrNull<Anchor>()
                                    ?.graphic
                                    ?.graphicData
                                    ?.any
                                    ?.forEach { any2 ->
                                        val ctWordprocessing = any2.toOrNull<JAXBElement<Any>>()?.value
                                        if (ctWordprocessing is CTWordprocessingShape) {
                                            findText(ctWordprocessing) { element, text -> afterFind(element, text) }
                                        } else if (ctWordprocessing is CTWordprocessingGroup) {
                                            ctWordprocessing.toOrNull<CTWordprocessingGroup>()
                                                ?.wspOrGrpSpOrGraphicFrame
                                                ?.forEach { f ->
                                                    findText(f) { element, text -> afterFind(element, text) }
                                                }
                                        }
                                    }
                            }
                    }
                }
            }

        findTextDone()
    }

    fun handleChildren(document: Document) {
        val needDeleteList = mutableListOf<DocxNode>()

        val children = document.children.toMutableList()
        val group = docxBean.group ?: mutableListOf()

        var cStartIndex = -1
        var cEndIndex = -1
        var groupIndex = -1

        run handleShow@{
            children.forEachIndexed { index, node ->
                val txt = node.content.toOrNull<P>()?.getText() ?: ""

                if (cStartIndex == -1) {
                    val fIndex = group.indexOfFirst { txt.contains(it.haveStart) }
                    if (fIndex >= 0) {
                        cStartIndex = index
                        groupIndex = fIndex
                    }
                } else {
                    val fIndex = group.indexOfFirst { txt.contains(it.haveEnd) }
                    if (fIndex == groupIndex) {
                        cEndIndex = index
                    }
                }

                if (cStartIndex != -1 && cEndIndex != -1 && groupIndex != -1) {
                    val g = group.removeAt(groupIndex)
                    logD("group=${g}")

                    if (g.hideChild) {
                        (cStartIndex..cEndIndex).forEach { needDeleteList.add(children[it]) }
                    } else {
                        needDeleteList.add(children[cStartIndex])
                        needDeleteList.add(children[cEndIndex])

                        val template = mutableListOf<DocxNode>()
                        (cStartIndex + 1 until cEndIndex).forEach { template.add(children[it]) }

                        val replaceList = g.replaceList ?: mutableListOf()
                        if (replaceList.isNotEmpty()) {
                            if (template.isNotEmpty()) {
                                var addIndex = cEndIndex
                                val c = template.first().content.toOrNull<Child>()
                                if (c != null) {
                                    val templateAll = mutableListOf<List<*>>()
                                    templateAll.add(template.map { it.content })

                                    (0 until replaceList.size - 1).forEach { _ ->
                                        c.parent.toOrNull<ContentAccessor>()?.content?.addAll(
                                            addIndex,
                                            template
                                                .map { XmlUtils.unmarshalString(XmlUtils.marshaltoString(it.content)) }
                                                .also { templateAll.add(it) },
                                        )
                                        addIndex += template.size
                                    }

                                    replaceList.forEachIndexed { indexR, replace ->
                                        val t = templateAll[indexR]
                                        replace.forEach { nowR ->
                                            val pList = t.mapNotNull { it.toOrNull<P>() }
                                            val p = pList.find { it.getText().contains(nowR.replace) }
                                            if (p != null) {
                                                val textViews = p.getTextViews()
                                                val replaceStr = nowR.replace
                                                val data = nowR.data
                                                val preciselyTv = textViews.find { it.value.contains(replaceStr) }
                                                if (preciselyTv != null) {
                                                    preciselyTv.value = preciselyTv.value.replace(replaceStr, data)
                                                } else {
                                                    var handled = false
                                                    val runTexts = textViews.map { it.value }.toMutableList()

                                                    run handle@{
                                                        runTexts.forEachIndexed { fromIndex, _ ->
                                                            (fromIndex + 1..runTexts.size).forEach { toIndex ->
                                                                val textOld = runTexts.subList(fromIndex, toIndex).joinToString("")
                                                                if (textOld == replaceStr) {
                                                                    var setDone = false
                                                                    (fromIndex until toIndex).forEach { i ->
                                                                        val run = textViews.getOrNull(i)
                                                                        if (!setDone) {
                                                                            setDone = true
                                                                            run?.value = data
                                                                        } else {
                                                                            run?.value = ""
                                                                        }
                                                                    }
                                                                    handled = true
                                                                    return@handle
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (!handled) {
                                                        val setTv =
                                                            textViews.find { !it.value.isNullOrEmpty() && it.value.isNotBlank() }?.apply { value = data }
                                                        (0 until textViews.size).forEach {
                                                            val tv = textViews[it]
                                                            if (setTv != null && tv != setTv) {
                                                                tv.apply {
                                                                    if (!value.isNullOrEmpty() && value.isNotBlank()) {
//                                                                    removeForParent()
                                                                        value = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    cStartIndex = -1
                    cEndIndex = -1
                    groupIndex = -1
                }
            }
        }

        needDeleteList.forEach { it.content.toOrNull<Child>()?.removeForParent() }
    }

    private fun findText(
        value: Any?,
        findListener: ((element: JAXBElement<Any>, text: Text?) -> Unit)? = null,
    ) {
        value.toOrNull<CTWordprocessingShape>()
            ?.txbx
            ?.txbxContent
            ?.content
            ?.forEach { c1 ->
                c1.toOrNull<P>()?.content?.forEach { c2 ->
                    c2.toOrNull<R>()?.content?.forEach { c3 ->
                        val element = c3.toOrNull<JAXBElement<Any>>()
                        if (element != null) {
                            val text = element.value?.toOrNull<Text>()
                            findListener?.invoke(element, text)
                        }
                    }
                }
            }
    }

    private val afList = mutableListOf<Pair<JAXBElement<Any>, Text?>>()
    fun afterFind(element: JAXBElement<Any>, text: Text?) {
        afList.add(element to text)
    }

    fun findTextDone() {
        afList.forEach { pair ->
            val text = pair.second
            val txt = text?.value ?: ""
            logD("findText $txt")
            if (txt.isEmpty()) return

            docxBean.ac?.forEach {
                if (it.checkHave && (txt.contains(it.haveStart) || txt.contains(it.haveEnd))) {
                    text?.findParent(P::class)?.removeForParent()
                } else if (txt.contains(it.replace)) {
                    val needHide = it.data.isEmpty()
                    if (needHide) {
                        text?.findParent(P::class)?.removeForParent()
                    } else {
                        if (it.type == DocxACBean.TYPE_TEXT) {
                            text?.value = txt.replace(it.replace, it.data)
                        } else if (it.type == DocxACBean.TYPE_IMAGE) {
                            text?.value = txt.replace(it.replace, "")
                        }
                    }
                }
            }
        }
        afList.clear()
    }
}
