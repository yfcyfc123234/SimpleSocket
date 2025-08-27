package com.yfc.test.docx.server

import com.yfc.com.yfc.socket.ext.TIME_PATTERN_YYYY_MM3
import com.yfc.com.yfc.socket.ext.TIME_PATTERN_YYYY_MM_DD
import com.yfc.com.yfc.socket.ext.string2Time
import com.yfc.com.yfc.socket.ext.time2String
import java.util.concurrent.TimeUnit

const val TIME_SO_FAR = "至今"
const val TIME_NOT_EDIT = "未填写"

internal fun timePart(startTime: String?, endTime: String?, pattern: String = TIME_PATTERN_YYYY_MM3): String {
    return if (startTime.isNullOrEmpty() || startTime == TIME_NOT_EDIT
        || endTime.isNullOrEmpty() || endTime == TIME_NOT_EDIT
    ) {
        ""
    } else {
        "${handleTimePart(startTime, pattern)}-${handleTimePart(endTime, pattern)}"
    }
}

private fun handleTimePart(timeStr: String?, pattern: String): String {
    return when {
        timeStr.isNullOrEmpty() -> TIME_NOT_EDIT
        timeStr == "0" || timeStr == TIME_SO_FAR -> TIME_SO_FAR
        else -> {
            val timeUnit = TimeUnit.MILLISECONDS
            timeStr.string2Time(TIME_PATTERN_YYYY_MM_DD, timeUnit).time2String(pattern, timeUnit)
        }
    }
}

internal fun handleTimeSoFar(timeStr: String?): String? = if (timeStr == TIME_SO_FAR) null else timeStr

