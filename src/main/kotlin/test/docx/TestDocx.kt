package test.docx

import com.yfc.com.yfc.socket.ext.logE
import com.yfc.test.docx.*
import jakarta.xml.bind.JAXBElement
import org.docx4j.XmlUtils
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingGroup.CTWordprocessingGroup
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape
import org.docx4j.dml.wordprocessingDrawing.Anchor
import org.docx4j.jaxb.Context
import org.docx4j.mce.AlternateContent
import org.docx4j.openpackaging.io.SaveToZipFile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.wml.*
import java.io.File

object TestDocx {
    private val keywordList by lazy { TestData.createTest() }

    private fun test(startP: P, endP: P, centerP: List<P>) {
        val a = startP.parent as? ContentAccessor
        a?.content?.remove(startP)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val f = Context.getWmlObjectFactory()

        val template = "C:/Users/Administrator/Desktop/resume_tpl1.docx"

        val save = true
        val output = "C:/Users/Administrator/Desktop/resume_tpl1_out.docx"

        val wordMLPackage = WordprocessingMLPackage.load(File(template))
        val documentPart = wordMLPackage.mainDocumentPart

        val start = System.currentTimeMillis()

        documentPart.contents.apply {
            val list = tree()
                .children
                .filter { it.node.content is P && it.children.isNotEmpty() }

            list.forEach {
                val p = it.node.content as P
//                p.content.clear()
            }

            descendants
                .also {
                    val list = it.toMutableList()
                    logE(list)
                }
                .filter { it.toString().isEmpty() }
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

        //需要替换的map
        val mappings = HashMap<String, String>()
        mappings["name"] = "张三"
        mappings["userAvatar"] = "25"
        mappings["time"] = "qlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlqqlq"

//        var wmlTemplateString = XmlUtils.marshaltoString(documentPart.contents, true, false, documentPart.jaxbContext)
//        mappings.forEach {
//            if (it.key.isNotEmpty()) {
//                val p = "\\$\\{${it.key}}"
//                wmlTemplateString = wmlTemplateString.replaceFirst(Regex(p), it.value)
//            }
//        }
//        documentPart.contents = XmlUtils.unwrap(XmlUtils.unmarshalString(wmlTemplateString, documentPart.jaxbContext)) as? Document

//        documentPart.variableReplace(mappings)

        val end = System.currentTimeMillis()
        val total = end - start
        logE("Time: $total")

        // Save it
        if (save) {
            val saver = SaveToZipFile(wordMLPackage)
            saver.save(output)
        } else {
            logE(XmlUtils.marshaltoString(documentPart.getJaxbElement(), true, true))
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

            keywordList.forEach {
                if (it.checkHave && (txt.contains(it.haveStart) || txt.contains(it.haveEnd))) {
                    text?.findParent(P::class)?.removeForParent()
                } else if (txt.contains(it.replace)) {
                    val needHide = it.data.isEmpty()
                    if (needHide) {
                        text?.findParent(P::class)?.removeForParent()
                    } else {
                        text?.value = txt.replace(it.replace, it.data)
                    }
                }
            }
        }
        afList.clear()
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
}
