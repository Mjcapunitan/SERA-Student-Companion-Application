package com.example.sera.screens.summarize

object SummaryDataStore {
    private var _currentSummary: String = ""
    private var _currentFileName: String = ""

    fun setSummary(summary: String) {
        _currentSummary = summary
    }

    fun getSummary(): String {
        return _currentSummary
    }

    fun setFileName(fileName: String) {
        _currentFileName = fileName
    }

    fun getFileName(): String {
        return _currentFileName
    }
}