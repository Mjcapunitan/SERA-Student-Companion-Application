package com.example.sera.screens.summarize

import androidx.lifecycle.ViewModel
import com.example.sera.data.RoomSummaryRepositoryState
import com.example.sera.data.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SummaryResultViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository
) : ViewModel() {

    suspend fun saveSummary(title: String, content: String): Long {
        val formattedTitle = if (!title.endsWith(" Summary")) "$title Summary" else title

        println("Saving Summary: Title: $formattedTitle, Content: ${content.take(100)}")

        return summaryRepository.saveSummary(formattedTitle, content)
    }
}
