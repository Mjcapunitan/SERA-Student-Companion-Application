package com.example.sera.common.value_objects.schedule

import android.os.Parcelable
import com.example.sera.common.Sera
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Parcelize
@JsonClass(generateAdapter = true)
data class Schedule(val from: Calendar, val to: Calendar, val room: String? = null) : Parcelable {
    fun getScheduleString(): String {
        val timeFormat = SimpleDateFormat(
            Sera.timeFormat,
            Locale.getDefault()
        )
        return "${timeFormat.format(from.time)} - ${timeFormat.format(to.time)}"
    }
}
