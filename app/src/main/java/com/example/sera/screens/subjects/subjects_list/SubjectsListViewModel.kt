package com.example.sera.screens.subjects.subjects_list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.domain.interactors.DeleteSubject
import com.example.sera.domain.observers.ObserveAllSubjects
import com.example.sera.common.value_objects.entities.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubjectsListViewModel @Inject constructor(
    val observeAllSubjects: ObserveAllSubjects,
    private val deleteSubject: DeleteSubject
) : ViewModel() {
    fun deleteSubject(
        subject: Subject,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteSubject.invoke(subject)
        }
    }
}
