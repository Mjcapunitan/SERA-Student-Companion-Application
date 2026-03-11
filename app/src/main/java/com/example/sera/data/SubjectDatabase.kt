package com.example.sera.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sera.data.converters.SubjectScheduleMapConverter
import com.example.sera.common.Sera
import com.example.sera.common.value_objects.entities.Subject

@Database(entities = [Subject::class], version = Sera.ITEM_DATABASE_VERSION)
@TypeConverters(SubjectScheduleMapConverter::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao

    companion object {
        fun getInstance(context: Context, databaseName: String): SubjectDatabase {
            return Room.databaseBuilder(
                context,
                SubjectDatabase::class.java,
                databaseName
            )
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .addMigrations(MIGRATION_5_6)
                .build()
        }
    }
}

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `subjects` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`title` TEXT NOT NULL, `daySchedulesMap` TEXT NOT NULL)")
    }
}

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `subjects` ADD COLUMN `colorValue` INTEGER NOT NULL DEFAULT -1")
    }
}

internal val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `subjects` ADD COLUMN `virtualMeetLink` TEXT")
    }
}
