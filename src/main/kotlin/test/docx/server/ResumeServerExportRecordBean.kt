package com.yfc.test.docx.server

data class ResumeServerExportRecordBean(
    var title: String? = null,
    var image: String? = null,
    var file_url: String? = null,
    var image_urls: MutableList<String>? = null,
) {
    fun toGenerateBean() = ResumeServerGenerateBean(file_url, image_urls, title)
}