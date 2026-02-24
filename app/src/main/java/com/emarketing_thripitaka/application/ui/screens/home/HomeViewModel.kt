package com.emarketing_thripitaka.application.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emarketing_thripitaka.application.data.DataLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadExams()
    }

    private fun loadExams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val exams = DataLoader.loadExams(getApplication())
            
            // Set initial selection if available
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
        val uniqueYears = papers.distinctBy { it.year } // List<Paper> for years dropdown

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
        // Extract year from label "Paper Title (2023)" -> "2023"
        // Logic from original HomeScreen:
        // options = uniqueYearPapers.map { "${it.paper_title} (${it.year})" }
        // val year = label.substringAfterLast("(").substringBefore(")")
        
        val yearStr = yearLabel.substringAfterLast("(").substringBefore(")")
        val exam = _uiState.value.exams.find { it.title == _uiState.value.selectedExam }
        val category = exam?.categories?.find { it.name == _uiState.value.selectedCategory }
        val allPapers = category?.papers ?: emptyList()
        
        val yearInt = yearStr.toIntOrNull() ?: 0
        val papersForYear = allPapers.filter { it.year == yearInt }
        
        // Parts logic
        val parts = papersForYear.filter { it.part != null }.map { "Part ${it.part}" }
        val selectedPaper = papersForYear.firstOrNull() // Default to first if main paper

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
        // Part Part 1 -> 1
        val partStr = partLabel.removePrefix("Part ")
        val exam = _uiState.value.exams.find { it.title == _uiState.value.selectedExam }
        val category = exam?.categories?.find { it.name == _uiState.value.selectedCategory }
        val allPapers = category?.papers ?: emptyList()
        val yearInt = _uiState.value.selectedYear.toIntOrNull() ?: 0
        
        val matchingPaper = allPapers.find { it.year == yearInt && it.part.toString() == partStr }

        _uiState.update {
            it.copy(
                selectedPart = partStr,
                selectedPaper = matchingPaper // Update selected paper to the specific part
            )
        }
    }
}
