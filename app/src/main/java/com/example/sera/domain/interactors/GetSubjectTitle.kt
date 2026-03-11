package com.example.sera.domain.interactors

import com.example.sera.data.RoomSubjectRepositoryState
import com.example.sera.data.SubjectRepository
import com.example.sera.utils.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSubjectTitle @Inject constructor(
    @RoomSubjectRepositoryState private val subjectRepository: StateFlow<SubjectRepository?>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(id: Int): String {
        return withContext(dispatcher) {
            subjectRepository.value?.getSubjectTitle(id) ?: ""
        }
    }
}