package com.emarketing_thripitaka.application.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emarketing_thripitaka.application.model.DownloadItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore by preferencesDataStore(name = "downloads_prefs")

class DownloadManager(private val context: Context) {
    private val DOWNLOADS_KEY = stringPreferencesKey("downloads_list")
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "DownloadManager"

    val downloads: Flow<List<DownloadItem>> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[DOWNLOADS_KEY] ?: "[]"
        try {
            json.decodeFromString<List<DownloadItem>>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding downloads metadata", e)
            emptyList()
        }
    }

    private fun generatePublicFileName(title: String, year: String?, part: String?): String {
        return buildString {
            append(title.replace(" ", "_").replace("/", "_").replace(":", "_"))
            if (!year.isNullOrBlank() && year != "null") {
                append("_$year")
            }
            if (!part.isNullOrBlank() && part != "null") {
                append("_Part_$part")
            }
            append(".pdf")
        }
    }

    suspend fun downloadPaper(
        url: String,
        title: String,
        category: String,
        exam: String,
        year: String?,
        part: String?,
        onProgress: (Float) -> Unit
    ): Result<DownloadItem> = withContext(Dispatchers.IO) {
        try {
            // Private file name uses timestamp to avoid collisions in internal storage
            val internalFileName = "${title.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            
            // 1. Download to App-Private Storage
            val privateFile = File(context.filesDir, internalFileName)
            
            val connection = URL(url).openConnection()
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()
            
            val totalSize = connection.contentLength.toLong()
            if (totalSize <= 0) {
                Log.w(TAG, "Content length is not available or zero for $url")
            }

            // Check storage before starting
            if (!hasEnoughSpace(totalSize)) {
                return@withContext Result.failure(Exception("Insufficient storage space"))
            }

            connection.getInputStream().use { input ->
                privateFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead: Long = 0
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        if (totalSize > 0) {
                            onProgress(totalRead.toFloat() / totalSize)
                        }
                    }
                }
            }

            // 2. Also save to Public Downloads folder with descriptive name
            val publicFileName = generatePublicFileName(title, year, part)
            saveToPublicDownloads(privateFile, publicFileName)

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val newItem = DownloadItem(
                url = url,
                title = title,
                exam = exam,
                year = year,
                part = part,
                category = category,
                uri = privateFile.absolutePath,
                date = sdf.format(Date())
            )

            saveDownloadMetadata(newItem)
            Result.success(newItem)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network failure: Unknown host", e)
            Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO failure during download", e)
            Result.failure(Exception("Failed to save file. Please check storage."))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during download", e)
            Result.failure(e)
        }
    }

    private fun hasEnoughSpace(requiredSize: Long): Boolean {
        val freeSpace = context.filesDir.freeSpace
        // Reserve at least 50MB extra space
        return freeSpace > (requiredSize + 50 * 1024 * 1024)
    }

    private fun saveToPublicDownloads(sourceFile: File, fileName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { destinationUri ->
                    resolver.openOutputStream(destinationUri)?.use { output ->
                        sourceFile.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                }
            } else {
                val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!publicDir.exists()) publicDir.mkdirs()
                val destFile = File(publicDir, fileName)
                sourceFile.copyTo(destFile, overwrite = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving to public downloads folder", e)
            // We don't fail the whole operation if public save fails, 
            // as private save was successful.
        }
    }

    private suspend fun saveDownloadMetadata(item: DownloadItem) {
        context.dataStore.edit { prefs ->
            val currentJson = prefs[DOWNLOADS_KEY] ?: "[]"
            val currentList = try {
                json.decodeFromString<MutableList<DownloadItem>>(currentJson).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            currentList.removeAll { it.url == item.url }
            currentList.add(item)
            prefs[DOWNLOADS_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun deleteDownload(item: DownloadItem) = withContext(Dispatchers.IO) {
        try {
            val file = File(item.uri)
            if (file.exists()) {
                file.delete()
            }
            context.dataStore.edit { prefs ->
                val currentJson = prefs[DOWNLOADS_KEY] ?: "[]"
                val currentList = try {
                    json.decodeFromString<List<DownloadItem>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
                currentList.removeAll { it.uri == item.uri }
                prefs[DOWNLOADS_KEY] = json.encodeToString(currentList)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting download", e)
        }
    }

    suspend fun exportToPublicStorage(item: DownloadItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(item.uri)
            if (!sourceFile.exists()) return@withContext Result.failure(Exception("Source file not found"))

            val fileName = generatePublicFileName(item.title, item.year, item.part)
            saveToPublicDownloads(sourceFile, fileName)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Manual export failed", e)
            Result.failure(e)
        }
    }
}
