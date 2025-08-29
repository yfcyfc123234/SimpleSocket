package com.yfc.com.yfc.docx.server

data class ResumeServerGenerateBean(
    var pdf: String? = null,
    var imgs: MutableList<String>? = null,
    var title: String? = null,
) {
//    val valid get() = pdf?.isNetworkUrl() == true && !imgs.isNullOrEmpty()
}