package com.example.sera.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sera.data.NotesDao
import com.example.sera.common.value_objects.entities.NotesEntity
import com.example.sera.data.converters.DateConverter

@Database(entities = [NotesEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}