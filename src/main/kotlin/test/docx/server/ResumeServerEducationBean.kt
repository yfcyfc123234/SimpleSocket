package com.yfc.test.docx.server

data class ResumeServerEducationBean(
    var eid: String? = "",
    var school_name: String? = "",
    var degree: String? = "",
    var major_name: String? = "",
    var description_str: String? = "",
    var start_time: String? = "",
    var end_time: String? = "",
) {
    val degreeAndMajor get() = "$degree $major_name"

//    fun timePart(pattern: String = TIME_PATTERN_YYYY_MM3): String = timePart(start_time, end_time, pattern)
}