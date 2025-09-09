package com.mylib.libcore.yfc.ext

import java.io.File

////////////////////////////////////////后缀名/////////////////////////////////////////////
const val FILE_SUFFIX_TXT = ".txt"
const val FILE_SUFFIX_PAG = ".pag"
const val FILE_SUFFIX_PNG = ".png"
const val FILE_SUFFIX_JPG = ".jpg"
const val FILE_SUFFIX_MP3 = ".mp3"
const val FILE_SUFFIX_MP4 = ".mp4"
const val FILE_SUFFIX_GIF = ".gif"
const val FILE_SUFFIX_ZIP = ".zip"
const val FILE_SUFFIX_WEBM = ".webm"
const val FILE_SUFFIX_WEBP = ".webp"
const val FILE_SUFFIX_MKV = ".mkv"
const val FILE_SUFFIX_MOV = ".mov"
const val FILE_SUFFIX_SRT = ".srt"
const val FILE_SUFFIX_SVG = ".svg"
const val FILE_SUFFIX_DOCX = ".docx"
const val FILE_SUFFIX_DOC = ".doc"
const val FILE_SUFFIX_PDF = ".pdf"
const val FILE_SUFFIX_PPT = ".ppt"
const val FILE_SUFFIX_PPTX = ".pptx"
//////////////////////////////////////后缀名///////////////////////////////////////////////

/**
 *
 * @author yfc
 * @since 2023/02/07 16:28
 * @version V1.0
 */
fun String?.toFile(): File? = getFileByPath(this)
fun getFileByPath(path: String?): File? = if (!path.isNullOrEmpty()) File(path) else null

/**
 * 不存在文件夹则创建之
 *
 * @return 文件夹路径
 */
fun File.getDirPath() = apply { createOrExistsDir() }.absolutePath ?: ""

fun File?.createOrExistsFile(): Boolean = if (this?.exists() == true && this.isFile) true else this?.createNewFile() == true
fun String?.createOrExistsFile(): Boolean = if (this.isNullOrEmpty()) false else toFile()?.createOrExistsFile() ?: false

fun File?.createOrExistsDir(): Boolean = if (this?.exists() == true && this.isDirectory) true else this?.mkdirs() == true
fun String?.createOrExistsDir(): Boolean = if (this.isNullOrEmpty()) false else toFile()?.createOrExistsDir() ?: false

fun File?.deleteCompat(): Boolean = if (this == null) false else {
    org.eclipse.persistence.tools.file.FileUtil.delete(this)
    true
}

fun String?.fileDeleteCompat(): Boolean = toFile()?.deleteCompat() ?: false