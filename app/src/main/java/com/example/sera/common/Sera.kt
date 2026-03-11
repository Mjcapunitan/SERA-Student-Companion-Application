package com.example.sera.common

import androidx.compose.ui.graphics.Color
import java.util.Calendar

object Sera {
    const val SUBJECTS_TABLE_NAME = "subjects"
    const val SUMMARIES_TABLE_NAME = "summaries"
    const val NOTES_TABLE_NAME = "notes"
    const val REMINDERS = 15
    const val REMINDER = "reminder"
    const val dateFormat = "EEE, MMM d, yyyy"
    const val dateFormatOnDatabase = "yyyyMMdd"
    const val dateTimeFormat = "EEE, MMM d, yyyy hh:mm aaa"
    const val timeFormat = "h:mm aaa"
    const val displayDateFormat = "EEEE, MMMM d, yyyy"
    const val dateFormatOnBackupFile = "yyMMddHHmmss"

    const val SELECTED_DATE = "selectedDate"

    const val R1_ENABLED = "r1_enabled"
    const val R1_HOUR_OF_DAY = "r1_hour_of_day"
    const val R1_MINUTE = "r1_minute"
    const val R1_DAYS = "r1_days"
    const val R2_ENABLED = "r2_enabled"
    const val R2_HOUR_OF_DAY = "r2_hour_of_day"
    const val R2_MINUTE = "r2_minute"
    const val R2_DAYS = "r2_days"

    const val ITEM_DATABASE_VERSION = 6

    const val SUBJECT_ID = "subject_id"
    const val SCHEDULE = "schedule"

    const val NICKNAME = "nickname"
    const val CLASS_REMINDER = "class_reminder"

    fun getThreadCountKey(): String {
        return "threadCount"
    }

    fun getDelegateKey(): String {
        return "delegate"
    }

    fun getCharLimitKey(): String {
        return "charLimit"
    }

    val daysHashMap = hashMapOf(
        "M" to Calendar.MONDAY,
        "T" to Calendar.TUESDAY,
        "W" to Calendar.WEDNESDAY,
        "Th" to Calendar.THURSDAY,
        "F" to Calendar.FRIDAY,
        "S" to Calendar.SATURDAY,
        "Su" to Calendar.SUNDAY
    )

    val subjectColorsLight = listOf(
        Color(0xFFf44336),
        Color(0xFFe91e63),
        Color(0xFF673ab7),
        Color(0xFF00bcd4),
        Color(0xFF009688),
        Color(0xFF4caf50),
        Color(0xFFcddc39),
        Color(0xFFffeb3b),
        Color(0xFFff9800),
        Color(0xFF795548),
        Color(0xFF607d8b),
    )
    val subjectColorsDark = listOf(
        Color(0xFFe57373),
        Color(0xFFf06292),
        Color(0xFF9575cd),
        Color(0xFF4dd0e1),
        Color(0xFF4db6ac),
        Color(0xFF81c784),
        Color(0xFFdce775),
        Color(0xFFfff176),
        Color(0xFFffb74d),
        Color(0xFFa1887f),
        Color(0xFF90a4ae),
    )
}