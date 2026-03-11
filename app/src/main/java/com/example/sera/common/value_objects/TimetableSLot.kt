package com.example.sera.common.value_objects

import com.example.sera.common.value_objects.entities.Subject
import java.util.*

data class TimetableSlot(
    val from: Calendar,
    val to: Calendar,
    val subject: Subject,
)