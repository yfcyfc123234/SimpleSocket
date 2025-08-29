package com.yfc.test

import java.io.File

object TestRename {
    @JvmStatic
    fun main(args: Array<String>) {
//        testRename("C:\\Users\\Administrator\\Desktop\\ezgif-split (5)", "frame", "ic_aw22_money_comes_anim")
        testFillXml("D:\\android_work_new\\driftassembly\\lib_widget\\src\\main\\res\\layout\\aw22_money_comes.xml", 22, "ic_aw22_money_comes_anim")
    }

    private fun testFillXml(filePath: String, count: Int, mipmap: String) {
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            val countPlaceholder = "_count_placeholder"
            val template = " <ImageView\n" +
                    "            android:id=\"@+id/iv_anim${countPlaceholder}\"\n" +
                    "            android:layout_width=\"match_parent\"\n" +
                    "            android:layout_height=\"wrap_content\"\n" +
                    "            android:adjustViewBounds=\"true\"\n" +
                    "            android:contentDescription=\"@null\"\n" +
                    "            android:scaleType=\"fitXY\"\n" +
                    "            android:src=\"@mipmap/${mipmap}${countPlaceholder}\" />"
            var newValue = ""
            (0 until count).forEach { index ->
                newValue += template.replace(countPlaceholder, "${index + 1}")
                newValue += "\n"
            }
            val data = file.readText().replace("</ViewFlipper>", "$newValue\n</ViewFlipper>")
            file.writeText(data)
        } else {
            println("文件${filePath}不存在")
        }
    }

    private fun testRename(dir: String, oldStr: String, newStr: String) {
        val file = File(dir)
        if (file.exists() && file.isDirectory) {
            file.listFiles()
                ?.filter { it.name.contains(oldStr) }
                ?.apply { println("替换${size}个文件") }
                ?.forEachIndexed { index, it ->
                    it.renameTo(File(file.absolutePath, "${newStr}${index + 1}.webp"))
                }
        } else {
            println("文件夹${dir}不存在")
        }
    }
}