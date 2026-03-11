package com.example.sera.common.value_objects

import androidx.annotation.StringRes
import com.example.sera.R

enum class ScheduleView {
    LIST,
    TIMETABLE,
}

sealed class ScheduleViewButtonItem(
    @StringRes text: Int,
    data: ScheduleView,
) : SegmentedButtonItem<ScheduleView>(text, data) {
    object List : ScheduleViewButtonItem(R.string.list, ScheduleView.LIST)
    object Timetable : ScheduleViewButtonItem(R.string.timetable, ScheduleView.TIMETABLE)
}

abstract class SegmentedButtonItem<T>(
    @StringRes val text: Int,
    val data: T,
)