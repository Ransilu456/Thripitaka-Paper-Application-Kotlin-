package com.emarketing_thripitaka.application.ui.screens.home

sealed interface HomeEvent {
    data class OnExamSelected(val exam: String) : HomeEvent
    data class OnCategorySelected(val category: String) : HomeEvent
    data class OnYearSelected(val year: String) : HomeEvent
    data class OnPartSelected(val part: String) : HomeEvent
}
