package com.yfc.com.yfc.socket.ext

import cn.hutool.core.date.DateUtil
import java.util.*
import java.util.concurrent.TimeUnit

const val TIME_PATTERN_YYYY_MM = "yyyy-MM"
const val TIME_PATTERN_YYYY_MM2 = "yyyy年MM月"
const val TIME_PATTERN_YYYY_MM3 = "yyyy.MM"

const val TIME_PATTERN_YYYY_MM_DD = "yyyy-MM-dd"
const val TIME_PATTERN_YYYY_MM_DD2 = "yyyyMMdd"
const val TIME_PATTERN_YYYY_MM_DD3 = "yyyy.MM.dd"
const val TIME_PATTERN_YYYY_MM_DD4 = "yyyy年MM月dd日"
const val TIME_PATTERN_YYYY_MM_DD5 = "yyyy.M.d"

const val TIME_PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
const val TIME_PATTERN_YYYY_MM_DD_HH_MM_SS2 = "yyyyMMddHHmmss"
const val TIME_PATTERN_YYYY_MM_DD_HH_MM_SS3 = "yyyy-M-d H:m:s"

const val TIME_PATTERN_YYYY_MM_DD_HH_MM = "yyyy年MM月dd日 HH:mm"
const val TIME_PATTERN_YYYY_MM_DD_HH_MM2 = "yyyy-MM-dd HH:mm"

const val TIME_PATTERN_MM_DD_HH_MM = "MM-dd HH:mm"
const val TIME_PATTERN_M_D_HH_MM = "M月d日HH:mm"
const val TIME_PATTERN_MM_DD = "MM月dd日"
const val TIME_PATTERN_MM_DD2 = "MM-dd"
const val TIME_PATTERN_MM_DD3 = "MM.dd"
const val TIME_PATTERN_M_D = "M月d日"
const val TIME_PATTERN_DD = "dd"

const val TIME_PATTERN_HH_MM = "HH:mm"
const val TIME_PATTERN_HH_MM2 = "HH mm"

const val TIME_PATTERN_H_M = "H时m分"
const val TIME_PATTERN_H_MM = "H时mm分"

const val TIME_PATTERN_YYYY = "yyyy"

const val TIME_PATTERN_YYYY_M_D = "yyyy年M月d日"


const val HH_MM_SS_SSS = "HH:mm:ss,SSS"
const val MM_SS_SSS = "mm:ss.SSS"

fun String?.time2String(
    pattern: String = TIME_PATTERN_YYYY_MM_DD,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    useThreeTen: Boolean = false,
): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        try {
            this.toLong().time2String(pattern, timeUnit, useThreeTen)
        } catch (e: Throwable) {
            ""
        }
    }
}

fun Long?.time2String(
    pattern: String = TIME_PATTERN_YYYY_MM_DD,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    useThreeTen: Boolean = false,
): String {
    runCatching {
        return if (useThreeTen) {
            ""
        } else {
            if (this == null) "" else DateUtil.format(Date(timeUnit.toMillis(this)), pattern)
        }
    }.onFailure {
        logE(it)
    }
    return ""
}

fun String?.string2Time(
    pattern: String = TIME_PATTERN_YYYY_MM_DD,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    useThreeTen: Boolean = false,
): Long {
    runCatching {
        return if (useThreeTen) {
            0L
        } else {
            if (this.isNullOrEmpty()) {
                0L
            } else {
                DateUtil.parse(this, pattern).let {
                    timeUnit.convert(it.millisecond().toLong(), TimeUnit.MILLISECONDS)
                }
            }
        }
    }.onFailure {
        logE(it)
    }
    return 0L
}

