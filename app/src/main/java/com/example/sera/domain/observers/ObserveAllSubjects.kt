package com.example.sera.domain.observers

import com.example.sera.data.RoomSubjectRepositoryState
import com.example.sera.data.SubjectRepository
import com.example.sera.common.value_objects.entities.Subject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveAllSubjects @Inject constructor(@RoomSubjectRepositoryState private val subjectRepository: StateFlow<SubjectRepository?>) {
    @ExperimentalCoroutinesApi
    operator fun invoke(): Flow<List<Subject>?> {
        return subjectRepository.flatMapLatest {
            it?.observeAllSubjects() ?: flowOf(null)
        }
    }
}