package com.yfc.test.docx.server

data class ResumeServerHonorBean(
    var hid: String? = "",
    var honor_name: String? = "",
    var award_time: String? = "",
) {
//    fun awardTime(pattern: String = TIME_PATTERN_YYYY_MM) =
//        award_time.string2Time(TIME_PATTERN_YYYY_MM_DD, TimeUnit.MILLISECONDS).time2String(pattern, TimeUnit.MILLISECONDS)
}