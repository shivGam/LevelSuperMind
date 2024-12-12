package com.example.levelmind.viewmodals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelmind.data.repositories.DownloadRepository
import com.example.levelmind.data.repositories.MediaRepository

class MediaViewModelFactory(
    private val mediaRepository: MediaRepository,
    private val downloadRepository: DownloadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            MediaViewModel(mediaRepository,downloadRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}