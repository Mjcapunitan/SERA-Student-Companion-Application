package com.example.sera.common.value_objects.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sera.common.Sera.SUMMARIES_TABLE_NAME
import java.util.Date

@Entity(tableName = SUMMARIES_TABLE_NAME)
data class SummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Date = Date()
)
