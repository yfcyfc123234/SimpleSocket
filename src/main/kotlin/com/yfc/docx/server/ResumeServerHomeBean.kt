package com.yfc.com.yfc.docx.server

data class ResumeServerHomeBean(
    var tips: String? = "5745",
    var resume: ResumeServerListBean? = null,
    var list: MutableList<ResumeServerTemplateListBean>? = null,
)

