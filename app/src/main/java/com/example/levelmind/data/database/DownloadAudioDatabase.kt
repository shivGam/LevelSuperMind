package com.example.levelmind.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


    @Database(entities = [DownloadedAudioEntity::class], version = 1, exportSchema = false)
    abstract class DownloadAudioDatabase : RoomDatabase() {
        abstract fun downloadedDao(): DownloadDao

        companion object {
            @Volatile
            private var INSTANCE: DownloadAudioDatabase? = null

            fun getInstance(context: Context): DownloadAudioDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        DownloadAudioDatabase::class.java,
                        "downloaded_songs_database"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }