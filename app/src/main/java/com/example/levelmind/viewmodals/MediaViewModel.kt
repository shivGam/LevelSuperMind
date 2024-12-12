package com.example.levelmind.viewmodals

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelmind.data.database.DownloadedAudioEntity
import com.example.levelmind.data.models.AudioModelItem
import com.example.levelmind.data.repositories.DownloadRepository
import com.example.levelmind.data.repositories.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel(
    private val repository: MediaRepository,
    private val downloadRepository: DownloadRepository
): ViewModel(){
    private val _audioList = MutableLiveData<List<AudioModelItem>>(emptyList())
    val audioList: LiveData<List<AudioModelItem>> = _audioList

    private val _downloadedAudioList = MutableLiveData<List<DownloadedAudioEntity>>(emptyList())
    val downloadedAudioList: LiveData<List<DownloadedAudioEntity>> = _downloadedAudioList

    val downloadedAudio: LiveData<List<DownloadedAudioEntity>> = downloadRepository.allDownloadedSongs
    init {
        fetchAudio()
        fetchDownloaded()
    }

    private fun fetchDownloaded() {
        viewModelScope.launch {
            downloadRepository.allDownloadedSongs.observeForever{
                _downloadedAudioList.postValue(it)
                Log.d("MediaViewModel", "Downloaded songs fetched: ${it.size}")
            }
        }
    }

    private fun fetchAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = repository.getSongs()
                if (response.isSuccessful) {
                    _audioList.postValue(response.body())
                    Log.d("MediaViewModel", "Songs fetched successfully")
                } else {
                    Log.e("MediaViewModel", "Error: ${response.code()} ${response.message()}")
                }
            }catch (e : Exception){
                Log.e("MediaViewModel", "Exception: ${e.message}")
            }
        }
    }

    suspend fun deleteDownloadedAudio(audioId: String) {
        return downloadRepository.deleteDownloadedAudio(audioId)
    }
    suspend fun isAudioDownloaded(audioId: String): Boolean {
        return downloadRepository.isAudioDownloaded(audioId)
    }

}