package com.emarketing_thripitaka.application.ui.screens.home

import com.emarketing_thripitaka.application.data.BookmarkedPaper
import com.emarketing_thripitaka.application.model.Exam
import com.emarketing_thripitaka.application.model.Paper

data class SearchResultPaper(
    val examTitle: String,
    val categoryName: String,
    val paper: Paper
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val exams: List<Exam> = emptyList(),
    val selectedExam: String = "",
    val selectedCategory: String = "",
    val selectedYear: String = "",
    val selectedPart: String = "",
    val availableCategories: List<String> = emptyList(),
    val availableYears: List<Paper> = emptyList(),
    val availableParts: List<String> = emptyList(),
    val selectedPaper: Paper? = null,
    val searchQuery: String = "",
    val searchResults: List<SearchResultPaper> = emptyList(),
    val isSearchActive: Boolean = false,
    val bookmarks: List<BookmarkedPaper> = emptyList(),
    val showBookmarksOnly: Boolean = false
)
