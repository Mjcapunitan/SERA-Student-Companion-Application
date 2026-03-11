package com.example.sera.domain.interactors

import com.example.sera.data.RoomSubjectRepositoryState
import com.example.sera.data.SubjectRepository
import com.example.sera.utils.di.IoDispatcher
import com.example.sera.common.value_objects.entities.Subject

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSubject @Inject constructor(
    @RoomSubjectRepositoryState private val subjectRepository: StateFlow<SubjectRepository?>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(id: Int): Subject? {
        return withContext(dispatcher) {
            subjectRepository.value?.getSubject(id)
        }
    }
}