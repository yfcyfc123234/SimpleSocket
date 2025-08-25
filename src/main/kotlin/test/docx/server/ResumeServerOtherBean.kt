package com.yfc.test.docx.server

data class ResumeServerOtherBean(
    var skill: String? = "",
    var language: String? = "",
    var certificate: String? = "",
    var activity: String? = "",
    var hobby: String? = "",
) {
    val skillStr get() = "技能:${skill}"
    val languageStr get() = "语言:${language}"
}
