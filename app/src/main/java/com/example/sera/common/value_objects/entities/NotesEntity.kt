package com.example.sera.common.value_objects.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sera.common.Sera.NOTES_TABLE_NAME
import java.util.Date

@Entity(tableName = NOTES_TABLE_NAME)
data class NotesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Date = Date()
)
