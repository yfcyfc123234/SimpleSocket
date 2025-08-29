package com.yfc.com.yfc.docx.server

import com.yfc.com.yfc.socket.ext.TIME_PATTERN_YYYY_MM3

data class ResumeServerProjectBean(
    var pid: String? = "",
    var project_name: String? = "",
    var role_name: String? = "",
    var description_str: String? = "",
    var start_time: String? = "",
    var end_time: String? = "",
) {
    fun timePart(pattern: String = TIME_PATTERN_YYYY_MM3): String = timePart(start_time, end_time, pattern)
}