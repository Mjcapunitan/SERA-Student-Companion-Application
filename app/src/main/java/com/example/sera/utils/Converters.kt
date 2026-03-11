package com.example.sera.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)

    @TypeConverter
    fun fromQuestionResults(questionResults: List<QuestionResult>): String {
        return gson.toJson(questionResults)
    }

    @TypeConverter
    fun toQuestionResults(json: String): List<QuestionResult> {
        val type = object : TypeToken<List<QuestionResult>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return dateFormat.format(date)
    }

    @TypeConverter
    fun toDate(value: String): Date {
        return dateFormat.parse(value) ?: Date()
    }
}