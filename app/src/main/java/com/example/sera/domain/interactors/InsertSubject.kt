package com.example.sera.domain.interactors

import android.content.Context
import com.example.sera.data.RoomSubjectRepositoryState
import com.example.sera.data.SubjectRepository
import com.example.sera.utils.di.IoDispatcher
import com.example.sera.common.value_objects.entities.Subject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertSubject @Inject constructor(
    @RoomSubjectRepositoryState private val subjectRepository: StateFlow<SubjectRepository?>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(subject: Subject) {
        withContext(dispatcher) {
            subjectRepository.value?.insert(subject)
        }
    }
}