package com.example.sera.common.value_objects

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = false)
enum class Day(val stringValue: String) {
    MONDAY("M"),
    TUESDAY("T"),
    WEDNESDAY("W"),
    THURSDAY("Th"),
    FRIDAY("F"),
    SATURDAY("S"),
    SUNDAY("Su")
}

fun getDay(dayOfWeek: Int) : Day? {
    return when (dayOfWeek) {
        Calendar.MONDAY -> Day.MONDAY
        Calendar.TUESDAY -> Day.TUESDAY
        Calendar.WEDNESDAY -> Day.WEDNESDAY
        Calendar.THURSDAY -> Day.THURSDAY
        Calendar.FRIDAY -> Day.FRIDAY
        Calendar.SATURDAY -> Day.SATURDAY
        Calendar.SUNDAY -> Day.SUNDAY
        else -> null
    }
}
fun getCalendarDay(day: Day) : Int {
    return when (day) {
        Day.MONDAY -> Calendar.MONDAY
        Day.TUESDAY -> Calendar.TUESDAY
        Day.WEDNESDAY -> Calendar.WEDNESDAY
        Day.THURSDAY -> Calendar.THURSDAY
        Day.FRIDAY -> Calendar.FRIDAY
        Day.SATURDAY -> Calendar.SATURDAY
        Day.SUNDAY -> Calendar.SUNDAY
    }
}