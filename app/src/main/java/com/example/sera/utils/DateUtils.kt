package com.example.sera.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun formatDate(pattern: String, date: Date): String {
    return SimpleDateFormat(
        pattern,
        Locale.getDefault()
    ).format(date)
}

fun Date.toLocalDate(): LocalDate {
    return toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}