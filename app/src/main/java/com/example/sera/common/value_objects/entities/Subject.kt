package com.example.sera.common.value_objects.entities
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sera.common.Sera.SUBJECTS_TABLE_NAME
import com.example.sera.utils.mutableMapForEnumWithValue
import com.example.sera.common.value_objects.schedule.DayScheduleMap
import com.example.sera.common.value_objects.schedule.getRecurringSchedules
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList

@Entity(tableName = SUBJECTS_TABLE_NAME)
@Parcelize
data class Subject @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   
    val title: String = "",
    val virtualMeetLink: String? = null,
    val colorValue: Int = -1,
    val daySchedulesMap: DayScheduleMap = mutableMapForEnumWithValue(::listOf),
) : Parcelable {
    fun getColor() = Color(colorValue)

    fun getSchedulesString(): String? {
        val schedules = daySchedulesMap.getRecurringSchedules()

        if (schedules.isNullOrEmpty()) return null

        val stringList: ArrayList<String> = arrayListOf()
        schedules.forEach {
            val daysString =
                it.days.set.sorted().joinToString(separator = " ") { day -> day.stringValue }
            stringList.add(
                "$daysString: ${it.schedule.getScheduleString()}"
            )
        }
        return stringList.joinToString(separator = "\n")
    }
}
