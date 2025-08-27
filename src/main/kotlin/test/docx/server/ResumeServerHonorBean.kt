package com.yfc.test.docx.server

import com.yfc.com.yfc.socket.ext.TIME_PATTERN_YYYY_MM
import com.yfc.com.yfc.socket.ext.TIME_PATTERN_YYYY_MM_DD
import com.yfc.com.yfc.socket.ext.string2Time
import com.yfc.com.yfc.socket.ext.time2String
import java.util.concurrent.TimeUnit

data class ResumeServerHonorBean(
    var hid: String? = "",
    var honor_name: String? = "",
    var award_time: String? = "",
) {
    fun awardTime(pattern: String = TIME_PATTERN_YYYY_MM) =
        award_time.string2Time(TIME_PATTERN_YYYY_MM_DD, TimeUnit.MILLISECONDS).time2String(pattern, TimeUnit.MILLISECONDS)
}