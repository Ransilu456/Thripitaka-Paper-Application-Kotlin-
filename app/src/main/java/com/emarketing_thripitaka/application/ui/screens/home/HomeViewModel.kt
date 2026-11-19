package com.emarketing_thripitaka.application.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emarketing_thripitaka.application.data.BookmarkManager
import com.emarketing_thripitaka.application.data.DataLoader
import com.emarketing_thripitaka.application.model.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val bookmarkManager = BookmarkManager(application)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadExams()
        observeBookmarks()
    }

    private fun loadExams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val exams = withContext(Dispatchers.IO) {
                DataLoader.loadExams(getApplication())
            }
            
            val initialExam = exams.firstOrNull()?.title ?: ""
            val initialCategories = exams.firstOrNull()?.categories?.map { it.name } ?: emptyList()

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    exams = exams,
                    selectedExam = initialExam,
                    availableCategories = initialCategories
                )
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            bookmarkManager.bookmarks.collect { bookmarks ->
                _uiState.update { it.copy(bookmarks = bookmarks) }
            }
        }
    }

    fun onExamSelected(examTitle: String) {
        val exam = _uiState.value.exams.find { it.title == examTitle }
        val categories = exam?.categories?.map { it.name } ?: emptyList()
        _uiState.update {
            it.copy(
                selectedExam = examTitle,
                selectedCategory = "",
                selectedYear = "",
                selectedPart = "",
                availableCategories = categories,
                availableYears = emptyList(),
                availableParts = emptyList(),
                selectedPaper = null
            )
        }
    }

    fun onCategorySelected(categoryName: String) {
        val exam = _uiState.value.exams.find { it.title == _uiState.value.selectedExam }
        val category = exam?.categories?.find { it.name == categoryName }
        val papers = category?.papers ?: emptyList()
        val uniqueYears = papers.distinctBy { it.year }

        _uiState.update {
            it.copy(
                selectedCategory = categoryName,
                selectedYear = "",
                selectedPart = "",
                availableYears = uniqueYears,
                availableParts = emptyList(),
                selectedPaper = null
            )
        }
    }

    fun onYearSelected(yearLabel: String) {
        val yearStr = yearLabel.substringAfterLast("(").substringBefore(")")
        val exam = _uiState.value.exams.find { it.title == _uiState.value.selectedExam }
        val category = exam?.categories?.find { it.name == _uiState.value.selectedCategory }
        val allPapers = category?.papers ?: emptyList()
        
        val yearInt = yearStr.toIntOrNull() ?: 0
        val papersForYear = allPapers.filter { it.year == yearInt }
        
        val parts = papersForYear.filter { it.part != null }.map { "Part ${it.part}" }
        val selectedPaper = papersForYear.firstOrNull()

        _uiState.update {
            it.copy(
                selectedYear = yearStr,
                selectedPart = "",
                availableParts = parts,
                selectedPaper = selectedPaper
            )
        }
    }

    fun onPartSelected(partLabel: String) {
        val partStr = partLabel.removePrefix("Part ")
        val exam = _uiState.value.exams.find { it.title == _uiState.value.selectedExam }
        val category = exam?.categories?.find { it.name == _uiState.value.selectedCategory }
        val allPapers = category?.papers ?: emptyList()
        val yearInt = _uiState.value.selectedYear.toIntOrNull() ?: 0
        
        val matchingPaper = allPapers.find { it.year == yearInt && it.part.toString() == partStr }

        _uiState.update {
            it.copy(
                selectedPart = partStr,
                selectedPaper = matchingPaper
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(searchQuery = query, searchResults = emptyList()) }
            return
        }

        val results = mutableListOf<SearchResultPaper>()
        val queryLower = query.lowercase().trim()

        _uiState.value.exams.forEach { exam ->
            exam.categories.forEach { category ->
                category.papers.forEach { paper ->
                    val matchTitle = paper.paper_title.lowercase().contains(queryLower)
                    val matchYear = paper.year.toString().contains(queryLower)
                    val matchExam = exam.title.lowercase().contains(queryLower)
                    val matchCategory = category.name.lowercase().contains(queryLower)

                    if (matchTitle || matchYear || matchExam || matchCategory) {
                        results.add(SearchResultPaper(examTitle = exam.title, categoryName = category.name, paper = paper))
                    }
                }
            }
        }

        _uiState.update { it.copy(searchQuery = query, searchResults = results) }
    }

    fun toggleSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active, searchQuery = "", searchResults = emptyList()) }
    }

    fun toggleBookmark(exam: String, category: String, paper: Paper) {
        viewModelScope.launch {
            bookmarkManager.toggleBookmark(exam, category, paper)
        }
    }

    fun toggleShowBookmarksOnly(show: Boolean) {
        _uiState.update { it.copy(showBookmarksOnly = show) }
    }
}
