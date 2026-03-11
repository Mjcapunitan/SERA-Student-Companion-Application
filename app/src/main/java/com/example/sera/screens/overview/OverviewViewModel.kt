package com.example.sera.screens.overview


import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.data.ManageAgendaDataDelegate
import com.example.sera.common.Sera
import com.example.sera.utils.di.DateToday
import com.example.sera.utils.di.HourOfDay
import com.example.sera.utils.formatDate
import com.example.sera.common.value_objects.ScheduleView
import com.example.sera.common.value_objects.ScheduleViewButtonItem
import com.example.sera.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    @AppPreferences private val sharedPreferences: SharedPreferences,
    @DateToday val dateToday: Calendar,
    @HourOfDay val hourOfDay: Int,
    manageAgendaDataDelegate: ManageAgendaDataDelegate
) : ViewModel(), ManageAgendaDataDelegate by manageAgendaDataDelegate {

    val dateTodayString = formatDate(Sera.displayDateFormat, dateToday.time)

    private var _scheduleView = mutableStateOf(getSavedScheduleView())
    val scheduleView: State<ScheduleViewButtonItem> = _scheduleView

    fun setScheduleView(scheduleViewButtonItem: ScheduleViewButtonItem) {
        _scheduleView.value = scheduleViewButtonItem
        sharedPreferences
            .edit()
            .putString(SCHEDULE_VIEW, scheduleViewButtonItem.data.name)
            .apply()
    }

    private fun getSavedScheduleView(): ScheduleViewButtonItem {
        val value = sharedPreferences.getString(SCHEDULE_VIEW, ScheduleView.TIMETABLE.name)
        return when (value?.let { ScheduleView.valueOf(it) } ?: ScheduleView.TIMETABLE) {
            ScheduleView.LIST -> ScheduleViewButtonItem.List
            ScheduleView.TIMETABLE -> ScheduleViewButtonItem.Timetable
        }
    }

    companion object {
        const val SCHEDULE_VIEW = "schedule_view"
    }
}