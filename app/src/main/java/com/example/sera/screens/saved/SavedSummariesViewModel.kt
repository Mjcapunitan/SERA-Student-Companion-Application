package com.example.sera.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.common.value_objects.entities.SummaryEntity
import com.example.sera.data.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedSummariesViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository
) : ViewModel() {

    val summaries: Flow<List<SummaryEntity>> = summaryRepository.getAllSummaries()

    fun deleteSummary(summaryId: Long) {
        viewModelScope.launch {
            summaryRepository.deleteSummaryById(summaryId)
        }
    }
}