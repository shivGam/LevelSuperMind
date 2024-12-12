package com.example.levelmind.api

import com.example.levelmind.data.models.AudioModelItem
import retrofit2.Response
import retrofit2.http.GET

interface AudioApi {
    @GET("/getSongs")
    suspend fun getSongs(): Response<List<AudioModelItem>>
}