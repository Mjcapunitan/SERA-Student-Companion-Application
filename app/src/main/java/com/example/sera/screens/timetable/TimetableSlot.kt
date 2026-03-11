package com.example.sera.screens.timetable

import androidx.compose.ui.unit.Dp
import com.example.sera.common.value_objects.entities.Subject
import java.util.*

data class TimetableSlot(
    val from: Calendar,
    val to: Calendar,
    val subject: Subject,
    val mainAxisOffset: Dp,
    val mainAxisSize: Dp,
    val crossAxisOffsetFraction: Float,
    val crossAxisSizeFraction: Float,
)
