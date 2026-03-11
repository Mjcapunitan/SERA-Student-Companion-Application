package com.example.sera.data.adapters

import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class DaysJsonAdapter {
    @FromJson
    fun daysFromJson(value: Set<Day>): Days {
        return Days(set = value.toMutableSet())
    }

    @ToJson
    fun daysToJson(days: Days): Set<Day> {
        return days.set
    }
}