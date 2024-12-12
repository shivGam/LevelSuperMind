package com.example.levelmind.viewmodals

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelmind.data.AudioModelItem
import com.example.levelmind.repositories.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

class MediaViewModel(
    private val repository: MediaRepository
): ViewModel(){
    private val _audioList = MutableLiveData<List<AudioModelItem>>(emptyList())
    val audioList: LiveData<List<AudioModelItem>> = _audioList

    init {
        fetchAudio()
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

}