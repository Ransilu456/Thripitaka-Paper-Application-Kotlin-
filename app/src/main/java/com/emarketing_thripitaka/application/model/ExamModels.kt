package com.emarketing_thripitaka.application.model

import kotlinx.serialization.Serializable

@Serializable
data class Paper(
    val part: Int? = null,
    val year: Int,
    val paper_title: String,
    val link: String
)

@Serializable
data class ExamCategory(
    val name: String,
    val papers: List<Paper>
)

@Serializable
data class Exam(
    val title: String,
    val categories: List<ExamCategory>
)

@Serializable
data class DownloadItem(
    val url: String? = null,
    val title: String,
    val exam: String,
    val year: String? = null,
    val part: String? = null,
    val category: String,
    val uri: String,
    val date: String? = null
)
