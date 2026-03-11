package com.example.sera.common.compose_ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.sera.common.value_objects.TimetableSlot
import java.util.*
import kotlin.math.floor

internal fun getListOffsets(
    list: List<TimetableSlot>,
    singleHourWidth: Dp,
    slotHeight: Dp,
    subjectLabelHeight: Dp,
    onUpdateBoxHeight: (Dp) -> Unit,
): Map<TimetableSlot, Pair<DpOffset, Dp>> {
    val sortedList =
        list.sortedBy { (it.from.get(Calendar.HOUR_OF_DAY) * 60) + it.from.get(Calendar.MINUTE) }

    val resultMap = mutableMapOf<TimetableSlot, Pair<DpOffset, Dp>>()
    val map: MutableMap<Int, Dp> = mutableMapOf()
    sortedList.forEach {
        val width = getSlotWidth(it, singleHourWidth)
        val x = getStartXValue(it.from, singleHourWidth)
        var y = 0.dp
        val keys = map.keys.sorted()

        var addNew = true
        for (key in keys) {
            if (x - (map[key] ?: 0.dp) >= 8.dp) {
                map[key] = (x + width)
                y = ((slotHeight * key) + ((subjectLabelHeight + 8.dp) * key)) + 8.dp
                addNew = false
                break
            }
        }
        if (addNew) {
            val lastKey = keys.lastOrNull() ?: -1
            map[lastKey + 1] = (x + width)
            y = ((slotHeight * (lastKey + 1)) + ((subjectLabelHeight + 8.dp) * (lastKey + 1))) + 8.dp
        }

        resultMap[it] = Pair(
            DpOffset(x = x, y = y),
            width,
        )
    }
    onUpdateBoxHeight((resultMap.values.maxOfOrNull { it.first.y }
        ?: 0.dp) + slotHeight + subjectLabelHeight + 16.dp)
    return resultMap
}

internal fun getStartXValue(calendar: Calendar, singleHourWidth: Dp): Dp {
    val hourOffset = calendar.get(Calendar.HOUR_OF_DAY) * singleHourWidth.value
    val minuteOffset = (calendar.get(Calendar.MINUTE) / 60f) * singleHourWidth.value
    return (hourOffset + minuteOffset + (singleHourWidth.value / 2)).dp
}

internal fun getSlotWidth(slot: TimetableSlot, singleHourWidth: Dp): Dp {
    val hourWidth =
        (slot.to.get(Calendar.HOUR_OF_DAY) - slot.from.get(Calendar.HOUR_OF_DAY)) * singleHourWidth.value
    val minuteWidth =
        ((slot.to.get(Calendar.MINUTE) - slot.from.get(Calendar.MINUTE)) / 60f) * singleHourWidth.value
    return (hourWidth + minuteWidth).dp
}

/**
 * TODO: Duplicate (com.dan.timetable)
 */
internal fun getHourText(hour: Int): String {
    val adjustedHour = hour - (floor(hour / 24f).toInt() * 24)
    return when {
        adjustedHour == 0 -> {
            "12:00 AM"
        }
        adjustedHour < 12 -> {
            "$adjustedHour:00 AM"
        }
        adjustedHour == 12 -> {
            "12:00 PM"
        }
        else -> {
            "${adjustedHour - 12}:00 PM"
        }
    }
}