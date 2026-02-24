package com.emarketing_thripitaka.application

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emarketing_thripitaka.application.ui.screens.home.HomeViewModel
import com.emarketing_thripitaka.application.ui.screens.downloads.DownloadsViewModel
import com.emarketing_thripitaka.application.ui.screens.paperinfo.PaperInfoViewModel
import com.emarketing_thripitaka.application.ui.screens.pdfviewer.PdfViewerViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        } else if (modelClass.isAssignableFrom(DownloadsViewModel::class.java)) {
             @Suppress("UNCHECKED_CAST")
            return DownloadsViewModel(application) as T
        } else if (modelClass.isAssignableFrom(PaperInfoViewModel::class.java)) {
             @Suppress("UNCHECKED_CAST")
            return PaperInfoViewModel(application) as T
        } else if (modelClass.isAssignableFrom(PdfViewerViewModel::class.java)) {
             @Suppress("UNCHECKED_CAST")
            return PdfViewerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
