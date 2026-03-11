package com.example.sera.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sera.data.SummaryDao
import com.example.sera.common.value_objects.entities.SummaryEntity
import com.example.sera.data.converters.DateConverter

@Database(entities = [SummaryEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class SummaryDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
}