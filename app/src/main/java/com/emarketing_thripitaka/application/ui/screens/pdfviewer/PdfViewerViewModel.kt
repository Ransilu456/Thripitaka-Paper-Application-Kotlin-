package com.emarketing_thripitaka.application.ui.screens.pdfviewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emarketing_thripitaka.application.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

sealed class PdfLoadState {
    object Idle : PdfLoadState()
    object Loading : PdfLoadState()
    data class Success(val file: File) : PdfLoadState()
    data class Error(val message: String) : PdfLoadState()
}

class PdfViewerViewModel(application: Application) : AndroidViewModel(application) {
    private val _loadState = MutableStateFlow<PdfLoadState>(PdfLoadState.Idle)
    val loadState: StateFlow<PdfLoadState> = _loadState

    fun loadPdf(paperLink: String) {
        val context = getApplication<Application>().applicationContext
        
        // Do not reload if already loaded successfully
        if (_loadState.value is PdfLoadState.Success && (loadState.value as PdfLoadState.Success).file.path == paperLink) return

        viewModelScope.launch {
            _loadState.value = PdfLoadState.Loading
            
            if (paperLink.startsWith("http") && !NetworkUtils.isInternetAvailable(context)) {
                _loadState.value = PdfLoadState.Error("අන්තර්ජාල සම්බන්ධතාවය පරීක්ෂා කරන්න (No Internet Connection)")
                return@launch
            }

            try {
                val file = withContext(Dispatchers.IO) {
                    if (paperLink.startsWith("http")) {
                        val fileName = "temp_viewer_${System.currentTimeMillis()}.pdf"
                        val cacheFile = File(context.cacheDir, fileName)
                        
                        // Clean up old temp files
                        context.cacheDir.listFiles()?.filter { it.name.startsWith("temp_viewer_") }?.forEach { it.delete() }

                        URL(paperLink).openStream().use { input ->
                            FileOutputStream(cacheFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        cacheFile
                    } else {
                        File(paperLink)
                    }
                }
                
                if (file.exists() && file.length() > 0) {
                    _loadState.value = PdfLoadState.Success(file)
                } else {
                    _loadState.value = PdfLoadState.Error("ප්‍රශ්න පත්‍රය සොයාගත නොහැක (File not found or empty)")
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.io.FileNotFoundException -> "සර්වර් එකෙහි ගොනුව නොමැත (File not found on server)"
                    is java.net.UnknownHostException -> "සර්වර් එකට සම්බන්ධ විය නොහැක (Cannot connect to server)"
                    else -> "දෝෂයකි: ${e.localizedMessage}"
                }
                _loadState.value = PdfLoadState.Error(errorMsg)
            }
        }
    }
}
