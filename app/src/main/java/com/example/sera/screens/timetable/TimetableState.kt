package com.example.sera.screens.timetable

import androidx.compose.runtime.Immutable
import com.example.sera.common.value_objects.entities.Subject

@Immutable
internal data class TimetableState(
    val subjects: List<Subject>? = null,
)
