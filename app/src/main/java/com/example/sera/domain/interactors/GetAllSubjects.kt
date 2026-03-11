package com.example.sera.domain.interactors

import com.example.sera.data.RoomSubjectRepositoryState
import com.example.sera.data.SubjectRepository
import com.example.sera.common.value_objects.entities.Subject
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAllSubjects @Inject constructor(@RoomSubjectRepositoryState private val subjectRepository: StateFlow<SubjectRepository?>) {
    suspend operator fun invoke(): List<Subject> {
        return subjectRepository.value?.getAllSubjects() ?: listOf()
    }
}