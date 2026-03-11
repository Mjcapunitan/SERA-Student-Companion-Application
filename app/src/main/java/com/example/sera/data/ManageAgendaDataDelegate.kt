package com.example.sera.data

import androidx.annotation.StringRes
import com.example.sera.R
import com.example.sera.domain.observers.ObserveAllSubjects
import com.example.sera.utils.di.DateToday
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.getDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

interface ManageAgendaDataDelegate {
    val messageRes: StateFlow<Int?>

    @ExperimentalCoroutinesApi
    val allSubjects: Flow<List<Subject>?>

    fun updateTime(calendar: Calendar?)

    val timetableDone: Flow<Boolean?>
}

class ManageAgendaDataDelegateImpl @Inject constructor(
    observeAllSubjects: ObserveAllSubjects,
    @DateToday dateToday: Calendar
) : ManageAgendaDataDelegate {

    private val _messageRes = MutableStateFlow<Int?>(null)

    override val messageRes: StateFlow<Int?> = _messageRes
    private val lastTimetableTime = MutableStateFlow<LastTimetableTime>(LastTimetableTime.NoData)
    private val currentTimetableTime = MutableStateFlow<Calendar?>(null)

    override fun updateTime(calendar: Calendar?) {
        currentTimetableTime.value = calendar
        _messageRes.value = getAgendaMessage()
    }

    override val timetableDone = combine(currentTimetableTime, lastTimetableTime) { currentTime, lastTime ->
        currentTime?.let { current ->
            when (lastTime) {
                is LastTimetableTime.Available -> {
                    lastTime.time?.let { last ->
                        if (current.get(Calendar.HOUR_OF_DAY) > last.get(Calendar.HOUR_OF_DAY)) {
                            true
                        } else if (current.get(Calendar.HOUR_OF_DAY) == last.get(Calendar.HOUR_OF_DAY)) {
                            current.get(Calendar.MINUTE) >= last.get(Calendar.MINUTE)
                        } else {
                            false
                        }
                    } ?: true
                }
                LastTimetableTime.NoData -> null
            }
        }
    }

    @ExperimentalCoroutinesApi
    override val allSubjects: Flow<List<Subject>?> =
        observeAllSubjects().onEach { subjects ->
            lastTimetableTime.value = LastTimetableTime.Available(
                subjects
                    ?.mapNotNull { subject ->
                        subject.daySchedulesMap[getDay(
                            Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                        )]?.maxOfOrNull { schedule ->
                            schedule.to
                        }
                    }
                    ?.maxOfOrNull { it }
            )
            _messageRes.value = getAgendaMessage()
        }



    @StringRes
    private fun getAgendaMessage(): Int? {
        return if (lastTimetableTime.value == null) {
            R.string.you_don_t_have_anything_scheduled_for_today
        } else {
            null
        }
    }
}

private sealed class LastTimetableTime {
    object NoData: LastTimetableTime()
    data class Available(val time: Calendar?): LastTimetableTime()
}