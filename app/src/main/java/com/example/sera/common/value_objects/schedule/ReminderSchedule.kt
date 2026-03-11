package com.example.sera.common.value_objects.schedule

import android.os.Parcelable
import com.example.sera.common.value_objects.Day
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ReminderSchedule(
    val day: Day,
    val fromMillis: Long,
    val toMillis: Long,
) : Parcelable
