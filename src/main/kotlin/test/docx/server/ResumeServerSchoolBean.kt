package com.yfc.test.docx.server

data class ResumeServerSchoolBean(
    var sid: String? = "",
    var experience_name: String? = "",
    var role_name: String? = "",
    var department: String? = "",
    var description_str: String? = "",
    var start_time: String? = "",
    var end_time: String? = "",
) {
    val departmentAndRole get() = "$department $role_name"

//    fun timePart(pattern: String = TIME_PATTERN_YYYY_MM3): String = timePart(start_time, end_time, pattern)
}