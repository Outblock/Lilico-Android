package io.outblock.lilico.utils

import android.annotation.SuppressLint
import android.text.format.DateUtils
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
fun String.gmtToTs(): Long {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply { timeZone = TimeZone.getTimeZone("GMT") }.parse(this).time
}


fun isToday(ts: Long): Boolean {
    if (Math.abs(System.currentTimeMillis() - ts) > DateUtils.DAY_IN_MILLIS) {
        return false
    }

    val other = Calendar.getInstance().apply {
        timeInMillis = ts
    }

    val now = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
    }

    return now.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

fun getStartOfToday(): Long {
    return DateTime.now().withTimeAtStartOfDay().millis
}

fun getStartOfTomorrow(): Long {
    return DateTime.now().plusDays(1).withTimeAtStartOfDay().millis
}

fun getStartOfDay(plusDays: Int = 0): Long {
    return DateTime.now().plusDays(plusDays).withTimeAtStartOfDay().millis
}

fun Long.plusDays(days: Int): Long {
    return this + DateUtils.DAY_IN_MILLIS * days
}

fun Long.dayOfYear(): Int {
    return DateTime(this).dayOfYear
}

fun Long.formatToDateTime(): String {
    return SimpleDateFormat.getDateTimeInstance().format(this)
}

fun Long.plusMonth(plus: Int): Long {
    val calendar = Calendar.getInstance().apply {
        time = Date(this@plusMonth)
        add(Calendar.MONTH, plus)
    }
    return calendar.timeInMillis
}

fun Long.plusYear(plus: Int): Long {
    val calendar = Calendar.getInstance().apply {
        time = Date(this@plusYear)
        add(Calendar.YEAR, plus)
    }
    return calendar.timeInMillis
}

fun Long.plusWeek(plus: Int): Long {
    val calendar = Calendar.getInstance().apply {
        time = Date(this@plusWeek)
        add(Calendar.DATE, plus * 7)
    }
    return calendar.timeInMillis
}
