package com.example.levelmind.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "downloaded_audio")
data class DownloadedAudioEntity(
    @PrimaryKey val id: String,
    val singer: String,
    val songName: String,
    val songBanner: String,
    val localFilePath: String
)
@Dao
interface DownloadDao {
    @Insert
    suspend fun insertAudio(audioFile : DownloadedAudioEntity)

    @Query("SELECT * FROM downloaded_audio")
    fun getAllDownloadedSongs(): LiveData<List<DownloadedAudioEntity>>

    @Query("SELECT COUNT(*) FROM downloaded_audio WHERE id = :audioId")
    suspend fun isAudioDownloaded(audioId: String): Int

    @Query("DELETE FROM downloaded_audio WHERE id = :audioId")
    suspend fun deleteDownloadedAudio(audioId: String)
}