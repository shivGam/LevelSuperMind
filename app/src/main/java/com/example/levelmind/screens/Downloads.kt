package com.example.levelmind.screens

import MediaPlayBackOverlay
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.levelmind.data.database.DownloadedAudioEntity
import com.example.levelmind.data.models.AudioModelItem
import com.example.levelmind.viewmodals.MediaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun Downloads(
    mediaViewModel: MediaViewModel,
    onAudioSelected: (AudioModelItem) -> Unit
) {
    val context = LocalContext.current
    val downloadedAudioList by mediaViewModel.downloadedAudio.observeAsState(emptyList())

    // State to manage currently selected audio for playback
    var selectedAudio by remember { mutableStateOf<AudioModelItem?>(null) }

    val lavenderBackground = Color(0xFFF0E6FF)
    val lavenderPrimary = Color(0xFF9E7BFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lavenderBackground)
    ) {
        // Show loading indicator if list is empty
        if (downloadedAudioList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Downloaded Audio",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = lavenderPrimary,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        } else {
            // Downloaded Audio List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(downloadedAudioList) { downloadedAudio ->
                    // Convert DownloadedAudioEntity to AudioModelItem for playback
                    val audioItem = AudioModelItem(
                        _id = downloadedAudio.id,
                        songName = downloadedAudio.songName,
                        singer = downloadedAudio.singer,
                        songBanner = downloadedAudio.songBanner,
                        url = downloadedAudio.localFilePath
                    )

                    DownloadedAudioItem(
                        downloadedAudio = downloadedAudio,
                        mediaViewModel = mediaViewModel,
                        context = context,
                        backgroundColor = lavenderPrimary,
                        onAudioSelected = {
                            // Update the selected audio for playback
                            selectedAudio = audioItem
                        }
                    )
                }
            }
        }

        // Overlay for selected audio
        selectedAudio?.let { audio ->
            MediaPlayBackOverlay(
                audioItem = audio,
                mediaViewModel = mediaViewModel,
                onClose = { selectedAudio = null }
            )
        }
    }
}

@Composable
fun DownloadedAudioItem(
    downloadedAudio: DownloadedAudioEntity,
    mediaViewModel: MediaViewModel,
    context: Context,
    backgroundColor: Color,
    onAudioSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor.copy(alpha = 0.1f))
            .clickable { onAudioSelected() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork
        Image(
            painter = rememberAsyncImagePainter(downloadedAudio.songBanner),
            contentDescription = downloadedAudio.songName,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // Song Details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = downloadedAudio.songName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Text(
                text = downloadedAudio.singer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // Delete Button
        IconButton(
            onClick = {
                val file = File(downloadedAudio.localFilePath)
                if (file.exists()) {
                    file.delete()
                }

                // Coroutine to delete from database
                CoroutineScope(Dispatchers.IO).launch {
                    mediaViewModel.deleteDownloadedAudio(downloadedAudio.id)
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Download",
                tint = Color.Black
            )
        }
    }
}