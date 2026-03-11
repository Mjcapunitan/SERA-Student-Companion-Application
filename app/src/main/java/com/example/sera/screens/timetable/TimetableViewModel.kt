package com.example.sera.screens.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.domain.observers.ObserveAllSubjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class TimetableViewModel @Inject constructor(
    observeAllSubjects: ObserveAllSubjects
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val subjectsState = observeAllSubjects()

    val state: StateFlow<TimetableState> = combine(subjectsState) { subjects ->
        TimetableState(
            subjects = subjects.first()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TimetableState(),
    )
}