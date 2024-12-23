package com.example.levelmind.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.levelmind.api.RetrofitInstance
import com.example.levelmind.data.database.DownloadAudioDatabase
import com.example.levelmind.data.repositories.DownloadRepository
import com.example.levelmind.data.repositories.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val downloadRepository: DownloadRepository by lazy {
        DownloadRepository(DownloadAudioDatabase.getInstance(applicationContext).downloadedDao())
    }

    private val mediaRepository: MediaRepository by lazy {
        MediaRepository(RetrofitInstance.api)
    }

    override suspend fun doWork(): Result {
        val downloadUrl = inputData.getString("download_url")
        val songId = inputData.getString("song_id")

        return withContext(Dispatchers.IO) {
            try {
                // Validate inputs
                if (downloadUrl.isNullOrBlank() || songId.isNullOrBlank()) {
                    Log.e("DownloadWorker", "Invalid download URL or song ID")
                    return@withContext Result.failure(
                        workDataOf("ERROR" to "Invalid download parameters")
                    )
                }

                // Fetch song details
                val songResponse = mediaRepository.getSongs()
                val audioItem = songResponse.body()?.find { it._id == songId }

                // Open connection
                val url = URL(downloadUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                // Check response code
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("DownloadWorker", "HTTP error code: $responseCode")
                    return@withContext Result.failure(
                        workDataOf("ERROR" to "Download failed with HTTP $responseCode")
                    )
                }

                // Prepare content values for MediaStore
                val fileName = "$songId.mp3"
                val mimeType = "audio/mpeg"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Audio.Media.TITLE, fileName)
                    put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Audio.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/LevelSuperMind")
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }

                val resolver = applicationContext.contentResolver

                // Insert into MediaStore
                val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { mediaStoreUri ->
                    resolver.openOutputStream(mediaStoreUri)?.use { outputStream ->
                        connection.inputStream.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    resolver.update(mediaStoreUri, contentValues, null, null)

                    val localFilePath = getLocalFilePath(mediaStoreUri)

                    if (audioItem != null) {
                        downloadRepository.insertDownloadedSong(audioItem, localFilePath)
                    }

                    Log.d("DownloadWorker", "Download successful: $fileName")
                    Result.success()
                } ?: run {
                    Log.e("DownloadWorker", "Failed to create MediaStore entry")
                    Result.failure(
                        workDataOf("ERROR" to "Could not create media store entry")
                    )
                }
            } catch (e: Exception) {
                Log.e("DownloadWorker", "Download failed", e)
                Result.failure(
                    workDataOf(
                        "ERROR" to "Download exception: ${e.message}",
                        "EXCEPTION" to e.toString()
                    )
                )
            }
        }
    }

    private fun getLocalFilePath(uri: Uri): String {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        applicationContext.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        throw IllegalStateException("Could not retrieve local file path")
    }
}