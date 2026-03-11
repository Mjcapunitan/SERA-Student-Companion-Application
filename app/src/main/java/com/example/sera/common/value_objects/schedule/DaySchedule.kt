package com.example.sera.common.value_objects.schedule
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days


typealias DayScheduleMap = Map<Day, List<Schedule>>
typealias MutableDayScheduleMap = MutableMap<Day, List<Schedule>>

fun DayScheduleMap.getRecurringSchedules(): List<RecurringSchedule> {
    val map = mutableMapOf<Schedule, RecurringSchedule>()
    forEach {
        it.value.forEach { schedule ->
            if (map.contains(schedule)) {
                map[schedule]?.days?.set?.add(it.key)
            } else {
                map[schedule] =
                    RecurringSchedule(schedule = schedule, days = Days(mutableSetOf(it.key)))
            }
        }
    }
    return map.values.toList()
}