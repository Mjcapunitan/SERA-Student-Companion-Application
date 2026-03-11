package com.example.sera.data.converters

import androidx.room.TypeConverter
import com.example.sera.data.adapters.CalendarJsonAdapter
import com.example.sera.data.adapters.DaysJsonAdapter
import com.example.sera.common.Sera
import com.example.sera.utils.mutableMapForEnumWithValue
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.schedule.DayScheduleMap
import com.example.sera.common.value_objects.schedule.Schedule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.text.SimpleDateFormat
import java.util.*

internal class SubjectScheduleMapConverter {
    private val moshi: Moshi
        get() = Moshi.Builder()
            .add(CalendarJsonAdapter(SimpleDateFormat(Sera.timeFormat, Locale.getDefault())))
            .add(DaysJsonAdapter())
            .build()

    private val adapter: JsonAdapter<DayScheduleMap>
        get() {
            val listType = Types.newParameterizedType(List::class.java, Schedule::class.java)
            val type = Types.newParameterizedType(Map::class.java, Day::class.java, listType)
            return moshi.adapter(type)
        }

    @TypeConverter
    fun toDayScheduleMapString(value: DayScheduleMap): String {
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toDayScheduleMap(value: String): DayScheduleMap {
        val map = adapter.fromJson(value) ?: mutableMapForEnumWithValue(::listOf)
        return map
    }
}