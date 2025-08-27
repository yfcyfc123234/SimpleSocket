package test.docx

import com.yfc.com.yfc.socket.ext.logE
import com.yfc.test.docx.*
import jakarta.xml.bind.JAXBElement
import org.docx4j.XmlUtils
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingGroup.CTWordprocessingGroup
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape
import org.docx4j.dml.CTBlip
import org.docx4j.dml.CTBlipFillProperties
import org.docx4j.dml.CTRelativeRect
import org.docx4j.dml.CTStretchInfoProperties
import org.docx4j.dml.wordprocessingDrawing.Anchor
import org.docx4j.jaxb.Context
import org.docx4j.mce.AlternateContent
import org.docx4j.openpackaging.io.SaveToZipFile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage
import org.docx4j.vml.CTFill
import org.docx4j.vml.CTRect
import org.docx4j.vml.CTShape
import org.docx4j.vml.CTTextbox
import org.docx4j.wml.*
import org.jvnet.jaxb2_commons.ppp.Child
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

object DocxUtil {
    val factory by lazy { Context.getWmlObjectFactory() }
    private val docxBean by lazy { DocxBean.createTest() }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = "C:/Users/Administrator/Desktop/resume_tpl1.docx"
        val output = "C:/Users/Administrator/Desktop/resume_tpl1_out.docx"
        val output2 = "C:/Users/Administrator/Desktop/resume_tpl1_out.html"

        val (wordMLPackage, openTime) = measureTimedValue {
            WordprocessingMLPackage.load(File(input))
        }

        logE("openTime=${openTime.toLong(DurationUnit.MILLISECONDS)}")

        val handleTime = measureTime {
            handleImage(wordMLPackage)
            wordMLPackage.mainDocumentPart.contents.apply {
                handleAlternateContent(this)
                handleChildren(this)
            }
        }
        logE("handleTime=$handleTime")

        val saveTime = measureTime {
            val save = true
            if (save) {
                SaveToZipFile(wordMLPackage).save(output)

//                Docx4J.toHTML(HTMLSettings().apply { opcPackage = wordMLPackage }, FileOutputStream(File(output2)), 0)
//                Docx4J.toFO(
//                    FOSettings().apply { opcPackage = wordMLPackage },
//                    FileOutputStream(File(output2)),
////            Docx4J.FLAG_EXPORT_PREFER_NONXSL,
//                    0,
//                )
//        Docx4J.toPDF(wordMLPackage, FileOutputStream(File(output2)))
            } else {
                logE(XmlUtils.marshaltoString(wordMLPackage.mainDocumentPart.getJaxbElement(), true, true))
            }
        }
        logE("saveTime=$saveTime")
    }

    fun handleImage(wordMLPackage: WordprocessingMLPackage) {
        val imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, File("C:/Users/Administrator/Pictures/IPAdapter_00154_.png"))
        val acList = docxBean.ac?.filter { it.type == DocxACBean.TYPE_IMAGE } ?: mutableListOf()
        val alternateContentList = wordMLPackage.mainDocumentPart
            .descendants
            .filter { it.content is AlternateContent }
            .map { it.content as AlternateContent }
            .toMutableList()
        acList.forEach { ac ->
            alternateContentList.forEach {
                it.apply {
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
                                    ctWordprocessing.spPr
                                        ?.apply {
                                            blipFill = CTBlipFillProperties().apply {
                                                blip = CTBlip().apply { embed = imagePart.relLast.id }
                                                stretch = CTStretchInfoProperties().apply { fillRect = CTRelativeRect() }
                                            }
                                        }
                                }
                            }
                        }

                        is CTWordprocessingGroup -> {
                            ctWordprocessing.toOrNull<CTWordprocessingGroup>()
                                ?.wspOrGrpSpOrGraphicFrame
                                ?.forEach { f ->
                                    findText(f) { _, text ->
                                        if (text?.value == ac.replace) {
                                            (f as CTWordprocessingShape).spPr?.apply {
                                                blipFill = CTBlipFillProperties().apply {
                                                    blip = CTBlip().apply { embed = imagePart.relLast.id }
                                                    stretch = CTStretchInfoProperties().apply { fillRect = CTRelativeRect() }
                                                }
                                            }
                                        }
                                    }
                                }
                        }

                        else -> {
                            // nothing
                        }
                    }

                    val f1 = fallback
                        ?.any
                        ?.getOrNull(0)
                        ?.toOrNull<Pict>()
                        ?.anyAndAny
                        ?.getOrNull(0)
                        ?.toOrNull<CTShape>()
                        ?.pathOrFormulasOrHandles
                        ?.filter { f -> f is JAXBElement }
                        ?.map { m -> m as JAXBElement }

                    val f2 = fallback
                        ?.any
                        ?.getOrNull(0)
                        ?.toOrNull<Pict>()
                        ?.anyAndAny
                        ?.getOrNull(0)
                        ?.toOrNull<JAXBElement<*>>()
                        ?.value
                        ?.toOrNull<CTRect>()
                        ?.pathOrFormulasOrHandles
                        ?.filter { f -> f is JAXBElement }
                        ?.map { m -> m as JAXBElement }

                    val f = f1 ?: f2

                    val ctTextbox = f?.find { f -> f.value is CTTextbox }?.value as? CTTextbox
                    if (ctTextbox != null) {
                        if (ctTextbox.txbxContent.content.find { f -> (f as? P)?.getText() == ac.replace } != null) {
                            val ctFill = f.find { f -> f.value is CTFill }?.value as? CTFill
                            ctFill?.id = imagePart.relLast.id
                        }
                    }
                }
            }
        }
    }

    fun handleAlternateContent(document: Document) {
        document.descendants
            .toMutableList()
            .filter { (it.content as? P)?.getText()?.isEmpty() == true }
            .map { it.descendants.toMutableList() }
            .filter { !it.isEmpty() }
            .flatMap { it }
            .filter { it.content is AlternateContent }
            .map { it.content as AlternateContent }
            .forEach {
                it.choice.forEach { c ->
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
                val txt = (node.content as? P)?.toString() ?: ""

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
                    logE("g=${g}")

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
                                val c = template.first().content as? Child
                                if (c != null) {
                                    val templateAll = mutableListOf<List<*>>()
                                    templateAll.add(template.map { it.content })

                                    (0 until replaceList.size - 1).forEach { _ ->
                                        (c.parent as? ContentAccessor)?.content?.addAll(
                                            addIndex,
                                            template
                                                .map { XmlUtils.unmarshalString(XmlUtils.marshaltoString(it.content)) }
                                                .also { templateAll.add(it) },
                                        )
                                        addIndex += template.size
                                    }

                                    replaceList.forEachIndexed { index, replace ->
                                        val t = templateAll[index]
                                        replace.forEach { nowR ->
                                            val pList = t.mapNotNull { it as? P }
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
                                                        runTexts.forEachIndexed { fromIndex, string ->
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

        needDeleteList.forEach {
            (it.content as? Child)?.removeForParent()
        }
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
            logE("findText $txt")
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
