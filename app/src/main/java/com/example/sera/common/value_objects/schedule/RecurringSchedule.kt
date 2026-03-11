package com.example.sera.common.value_objects.schedule

import android.os.Parcelable
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.example.sera.utils.mutableMapForEnumWithValue
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RecurringSchedule(
    val schedule: Schedule,
    val days: Days,
) : Parcelable

fun List<RecurringSchedule>.getDaySchedules(): Map<Day, List<Schedule>> {
    val map = mutableMapForEnumWithValue<Day, MutableList<Schedule>>(::mutableListOf)
    forEach { recurringSchedule ->
        recurringSchedule.days.set.forEach { day ->
            map[day]?.add(recurringSchedule.schedule)
        }
    }
    return map
}
