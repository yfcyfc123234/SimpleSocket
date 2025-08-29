package com.yfc.com.yfc.docx.server

data class ResumeServerTemplateListBean(
    var id: String? = null,
    var name: String? = null,
    var image: String? = null,
    var is_collect: Int = 0,
) {
    @Transient
    var selected: Boolean = false
}