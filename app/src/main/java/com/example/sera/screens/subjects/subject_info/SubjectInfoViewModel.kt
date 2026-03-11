package com.example.sera.screens.subjects.subject_info

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.domain.interactors.DeleteSubject
import com.example.sera.domain.interactors.InsertSubject
import com.example.sera.domain.interactors.UpdateSubject
import com.example.sera.domain.observers.ObserveSubject
import com.example.sera.common.value_objects.entities.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Move [insertSubject], [updateSubject], and [deleteSubject]
 * logic to domain module
 */
@HiltViewModel
class SubjectInfoViewModel @Inject constructor(
    val observeSubject: ObserveSubject,
    private val insertSubject: InsertSubject,
    private val updateSubject: UpdateSubject,
    private val deleteSubject: DeleteSubject,
) : ViewModel() {

    fun insertSubject(
        subject: Subject,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            insertSubject.invoke(subject)
        }
    }

    fun updateSubject(
        newSubject: Subject,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateSubject.invoke(newSubject = newSubject)
        }
    }

    fun deleteSubject(
        subject: Subject,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteSubject.invoke(subject)
        }
    }
}