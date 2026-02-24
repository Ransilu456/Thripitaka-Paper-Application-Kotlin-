package com.emarketing_thripitaka.application.ui.screens.downloads

import android.app.Application
import android.print.PrintAttributes
import android.print.PrintManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emarketing_thripitaka.application.data.DownloadManager
import com.emarketing_thripitaka.application.model.DownloadItem
import com.emarketing_thripitaka.application.util.PdfPrintAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class DownloadsViewModel(application: Application) : AndroidViewModel(application) {

    private val downloadManager = DownloadManager(application)
    private val _uiState = MutableStateFlow(DownloadsUiState(isLoading = true))
    val uiState: StateFlow<DownloadsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            downloadManager.downloads.collectLatest { downloads ->
                _uiState.update { it.copy(isLoading = false, downloads = downloads) }
            }
        }
    }

    fun showDeleteConfirmation(item: DownloadItem) {
        _uiState.update { it.copy(showDeleteDialog = item) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteDialog = null) }
    }

    fun deleteDownload(item: DownloadItem) {
        viewModelScope.launch {
            downloadManager.deleteDownload(item)
            hideDeleteConfirmation()
            _uiState.update { it.copy(message = "Deleted successfully") }
        }
    }

    fun exportDownload(item: DownloadItem) {
        viewModelScope.launch {
            val result = downloadManager.exportToPublicStorage(item)
            if (result.isSuccess) {
                _uiState.update { it.copy(message = "Saved to Downloads folder") }
            } else {
                _uiState.update { it.copy(message = "Export failed: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun printDownload(context: Context, item: DownloadItem) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${item.title} Print Job"
        
        try {
            val file = File(item.uri)
            if (file.exists()) {
                printManager.print(
                    jobName,
                    PdfPrintAdapter(file),
                    PrintAttributes.Builder().build()
                )
            } else {
                _uiState.update { it.copy(message = "File not found for printing") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(message = "Printing failed: ${e.message}") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
