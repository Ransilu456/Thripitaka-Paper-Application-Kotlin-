package com.emarketing_thripitaka.application.ui.screens.downloads

import com.emarketing_thripitaka.application.model.DownloadItem

data class DownloadsUiState(
    val isLoading: Boolean = false,
    val downloads: List<DownloadItem> = emptyList(),
    val showDeleteDialog: DownloadItem? = null,
    val message: String? = null
)
