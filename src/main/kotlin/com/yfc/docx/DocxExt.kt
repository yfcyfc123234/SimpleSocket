package com.yfc.com.yfc.docx

import jakarta.xml.bind.JAXBElement
import org.docx4j.wml.ContentAccessor
import org.docx4j.wml.P
import org.docx4j.wml.Text
import org.jvnet.jaxb2_commons.ppp.Child
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

operator fun DocxNode.iterator(): MutableIterator<DocxNode> = object : MutableIterator<DocxNode> {
    private var index = 0
    override fun hasNext() = hasNext(index)
    override fun next(): DocxNode = next(index++)
    override fun remove(): Unit = throw UnsupportedOperationException()
}

val ContentAccessor.children: Sequence<DocxNode> get() = DocxNode.create(this).children
val DocxNode.children: Sequence<DocxNode>
    get() = object : Sequence<DocxNode> {
        override fun iterator() = this@children.iterator()
    }

val ContentAccessor.descendants: Sequence<DocxNode> get() = DocxNode.create(this).descendants
val DocxNode.descendants: Sequence<DocxNode>
    get() = Sequence { TreeIterator(children.iterator()) { child -> child.children.iterator() } }

internal class TreeIterator<T>(rootIterator: Iterator<T>, private val getChildIterator: ((T) -> Iterator<T>?)) : Iterator<T> {
    private val stack = mutableListOf<Iterator<T>>()

    private var iterator: Iterator<T> = rootIterator

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): T {
        val item = iterator.next()
        prepareNextIterator(item)
        return item
    }

    private fun prepareNextIterator(item: T) {
        val childIterator = getChildIterator(item)
        if (childIterator != null && childIterator.hasNext()) {
            stack.add(iterator)
            iterator = childIterator
        } else {
            while (!iterator.hasNext() && stack.isNotEmpty()) {
                iterator = stack.last()
                stack.removeLast()
            }
        }
    }
}


fun ContentAccessor.tree() = DocxNode.create(this).tree()
fun DocxNode.tree(): DocxNodeTree {
    val tree = DocxNodeTree(this, mutableListOf())
    if (!children.none()) children.toMutableList().forEach { tree.children.add(it.tree()) }
    return tree
}

class DocxNodeTree(var node: DocxNode, var children: MutableList<DocxNodeTree>) {
    override fun toString() = node.toString()
}

class DocxNode(var contentAccessor: ContentAccessor? = null, var any: Any? = null) {
    companion object {
        fun create(any: Any?) = when (any) {
            is ContentAccessor -> DocxNode(contentAccessor = any)
            else -> DocxNode(any = any)
        }
    }

    val content get() = contentAccessor ?: any

    override fun toString(): String {
        return if (content is P) {
            "${content!!::class.java.name}${(content as P).getText()}"
        } else {
            content.toString()
        }
    }

    fun hasNext(index: Int) = (contentAccessor?.content?.size ?: 0) > index
    fun next(index: Int) = create(contentAccessor?.content?.getOrNull(index))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified T : Any> Any?.toOrNull(): T? {
    contract { returnsNotNull() implies (this@toOrNull != null) }
    return if (this is T) to() else null
}

inline fun <reified T : Any> Any.to(): T = this as T

fun <T : Any> Child.findParent(classz: KClass<T>) = findParent(classz.java)
fun <T : Any> Child.findParent(classz: Class<T>): T? {
    val p = parent ?: return null
    return if (p.javaClass.name == classz.name) {
        p as T
    } else {
        if (p is Child) p.findParent(classz) else null
    }
}

fun Child.removeForParent() {
    val p = parent ?: return
    if (p is ContentAccessor) p.content?.remove(this) else (p as? Child)?.removeForParent()
}

fun P.getTextViews() = descendants
    .filter { it.content is JAXBElement<*> }
    .mapNotNull { (it.content as? JAXBElement<*>)?.value as? Text }
    .toMutableList()

fun P.getText() = getTextViews().joinToString("") { it.value }