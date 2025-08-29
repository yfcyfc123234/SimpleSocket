package com.yfc.docx.server

import com.yfc.socket.ext.TIME_PATTERN_YYYY_MM3

data class ResumeServerWorkBean(
    var wid: String? = "",
    var company_name: String? = "",
    var position_name: String? = "",
    var description_str: String? = "",
    var department: String? = "",
    var start_time: String? = "",
    var end_time: String? = "",
) {
    val departmentAndPosition get() = "$department $position_name"

    fun timePart(pattern: String = TIME_PATTERN_YYYY_MM3): String = timePart(start_time, end_time, pattern)
}