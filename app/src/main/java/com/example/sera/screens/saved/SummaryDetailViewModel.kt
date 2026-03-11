package com.example.sera.screens.saved

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.data.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SummaryState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = true,
    val error: String = ""
)

@HiltViewModel
class SummaryDetailViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _summaryState = MutableStateFlow(SummaryDetailState())
    val summaryState = _summaryState.asStateFlow()

    // Error state for delete operation
    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError = _deleteError.asStateFlow()

    fun loadSummary(summaryId: Long) {
        viewModelScope.launch {
            _summaryState.update { it.copy(isLoading = true) }
            try {
                val summary = summaryRepository.getSummaryById(summaryId)

                if (summary != null) {
                    _summaryState.update {
                        it.copy(
                            isLoading = false,
                            title = summary.title,
                            content = summary.content,
                            error = ""
                        )
                    }
                } else {
                    _summaryState.update {
                        it.copy(
                            isLoading = false,
                            error = "Summary not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _summaryState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error loading summary: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun deleteSummary(summaryId: Long): Boolean {
        var deleteSuccessful = false
        viewModelScope.launch {
            try {
                summaryRepository.deleteSummaryById(summaryId)
                _deleteError.value = null
                deleteSuccessful = true
            } catch (e: Exception) {
                _deleteError.value = "Failed to delete summary: ${e.message ?: "Unknown error"}"
                deleteSuccessful = false
            }
        }
        return deleteSuccessful
    }

    // Clear any error state
    fun clearDeleteError() {
        _deleteError.value = null
    }
}

// State class to hold the summary details
data class SummaryDetailState(
    val isLoading: Boolean = false,
    val title: String = "",
    val content: String = "",
    val error: String = ""
)