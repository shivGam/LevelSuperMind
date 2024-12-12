package com.example.levelmind.data.repositories

import com.example.levelmind.api.AudioApi
import com.example.levelmind.data.models.AudioModelItem
import retrofit2.Response

class MediaRepository(private val audioApi: AudioApi) {
    suspend fun getSongs() : Response<List<AudioModelItem>>{
        return audioApi.getSongs()
    }
}