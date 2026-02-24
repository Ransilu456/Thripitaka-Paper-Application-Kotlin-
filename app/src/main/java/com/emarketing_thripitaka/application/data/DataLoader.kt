package com.emarketing_thripitaka.application.data

import android.content.Context
import com.emarketing_thripitaka.application.model.Exam
import kotlinx.serialization.json.Json
import java.io.IOException

object DataLoader {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    fun loadExams(context: Context): List<Exam> {
        return try {
            val jsonString = context.assets.open("data/data.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<Exam>>(jsonString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
