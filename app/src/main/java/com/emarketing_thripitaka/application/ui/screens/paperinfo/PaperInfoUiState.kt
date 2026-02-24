package com.emarketing_thripitaka.application.ui.screens.paperinfo

data class PaperInfoUiState(
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val localPath: String? = null,
    val isCheckingDownload: Boolean = true
)
