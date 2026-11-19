package com.emarketing_thripitaka.application.ui.screens.paperinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emarketing_thripitaka.application.data.BookmarkManager
import com.emarketing_thripitaka.application.data.BookmarkedPaper
import com.emarketing_thripitaka.application.data.DownloadManager
import com.emarketing_thripitaka.application.model.DownloadItem
import com.emarketing_thripitaka.application.model.Paper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class PaperInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val downloadManager = DownloadManager(application)
    private val bookmarkManager = BookmarkManager(application)
    
    private val _downloadUrl = MutableStateFlow<String?>(null)
    private val _isDownloading = MutableStateFlow(false)
    private val _downloadProgress = MutableStateFlow(0f)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isSuccess = MutableStateFlow(false)

    val uiState: StateFlow<PaperInfoUiState> = combine(
        _downloadUrl,
        downloadManager.downloads,
        _isDownloading,
        _downloadProgress,
        _errorMessage,
        _isSuccess,
        bookmarkManager.bookmarks
    ) { args: Array<Any?> ->
        val url = args[0] as? String
        @Suppress("UNCHECKED_CAST")
        val downloads = args[1] as List<DownloadItem>
        val isDownloading = args[2] as Boolean
        val progress = args[3] as Float
        val error = args[4] as? String
        val success = args[5] as Boolean
        @Suppress("UNCHECKED_CAST")
        val bookmarks = args[6] as List<BookmarkedPaper>

        val localPath = if (url != null) {
            downloads.find { it.url == url || it.uri.contains(url.split("/").last()) }?.uri
                ?.let { path -> if (File(path).exists()) path else null }
        } else null

        val isBookmarked = if (url != null) {
            bookmarks.any { it.paper.link == url }
        } else false

        PaperInfoUiState(
            isDownloading = isDownloading,
            downloadProgress = progress,
            errorMessage = error,
            isSuccess = success,
            localPath = localPath,
            isCheckingDownload = false,
            isBookmarked = isBookmarked
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaperInfoUiState(isCheckingDownload = true)
    )

    fun initUrl(url: String) {
        val decodedLink = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
        _downloadUrl.value = decodedLink
    }

    fun toggleBookmark(exam: String, category: String, title: String, link: String, year: String?, part: String?) {
        val yrInt = year?.toIntOrNull() ?: 0
        val prtInt = part?.toIntOrNull()
        val paper = Paper(part = prtInt, year = yrInt, paper_title = title, link = link)
        viewModelScope.launch {
            bookmarkManager.toggleBookmark(exam, category, paper)
        }
    }

    fun downloadPaper(
        url: String,
        title: String,
        category: String,
        exam: String,
        year: String?,
        part: String?
    ) {
        if (_isDownloading.value) return

        val decodedLink = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
        val decodedTitle = title.replace("+", " ")

        viewModelScope.launch {
            _isDownloading.value = true
            _downloadProgress.value = 0f
            _errorMessage.value = null
            _isSuccess.value = false
            
            val result = downloadManager.downloadPaper(
                url = decodedLink,
                title = decodedTitle,
                category = category,
                exam = exam,
                year = year,
                part = part,
                onProgress = { progress ->
                    _downloadProgress.value = progress
                }
            )

            _isDownloading.value = false
            _isSuccess.value = result.isSuccess
            _errorMessage.value = result.exceptionOrNull()?.message
        }
    }
}
