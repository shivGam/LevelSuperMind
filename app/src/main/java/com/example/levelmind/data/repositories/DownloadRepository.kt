package com.example.levelmind.data.repositories

import androidx.lifecycle.LiveData
import com.example.levelmind.data.database.DownloadDao
import com.example.levelmind.data.database.DownloadedAudioEntity
import com.example.levelmind.data.models.AudioModelItem

class DownloadRepository(private val downloadDao: DownloadDao) {

    val allDownloadedSongs: LiveData<List<DownloadedAudioEntity>> = downloadDao.getAllDownloadedSongs()
    suspend fun insertDownloadedSong(audioItem: AudioModelItem, localFilePath: String) {
        val downloadedSong = DownloadedAudioEntity(
            id = audioItem._id,
            singer = audioItem.singer,
            songName = audioItem.songName,
            songBanner = audioItem.songBanner,
            localFilePath = localFilePath
        )
        downloadDao.insertAudio(downloadedSong)
    }

    suspend fun isAudioDownloaded(audioId: String): Boolean {
        return downloadDao.isAudioDownloaded(audioId) > 0
    }

    suspend fun deleteDownloadedAudio(audioId: String) {
        downloadDao.deleteDownloadedAudio(audioId)
    }
}