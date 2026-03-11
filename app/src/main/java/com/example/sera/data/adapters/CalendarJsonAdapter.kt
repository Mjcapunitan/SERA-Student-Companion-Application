package com.example.sera.data.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

internal class CalendarJsonAdapter(private val format: SimpleDateFormat) {
    @FromJson
    fun calendarFromJson(value: String): Calendar {
        return Calendar.getInstance().apply {
            try {
                format.parse(value)?.let { time = it }
            } catch (_: Exception) {}
        }
    }

    @ToJson
    fun eventToJson(calendar: Calendar): String {
        return format.format(calendar.time)
    }
}