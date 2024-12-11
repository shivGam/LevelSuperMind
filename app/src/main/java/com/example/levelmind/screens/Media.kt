package com.example.levelmind.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.levelmind.R
import com.example.levelmind.data.AudioModelItem
import com.example.levelmind.viewmodals.MediaViewModel
import java.io.File

@Composable
fun Media(mediaViewModel: MediaViewModel) {
    val audioList by mediaViewModel.audioList.observeAsState(emptyList())
    val context = LocalContext.current
    val downloadDir = context.filesDir

    // Scroll state for banner visibility
    val listState = rememberLazyListState()

    // Lavender color palette
    val lavenderBackground = Color(0xFFF0E6FF)
    val lavenderPrimary = Color(0xFF9E7BFF)

    // Determine banner visibility based on scroll position
    val isBannerVisible = listState.firstVisibleItemIndex == 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lavenderBackground)
    ) {
        Column {
            // Top Banner with Animated Visibility
            AnimatedVisibility(
                visible = isBannerVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.banner),
                        contentDescription = "Top Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay Gradient
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        lavenderBackground.copy(alpha = 0.8f)
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                }
            }

            // Show loading indicator if list is empty
            if (audioList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = lavenderPrimary,
                        modifier = Modifier.size(50.dp)
                    )
                }
            } else {
                // Audio List
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(audioList) { audioItem ->
                        AudioItem(
                            audioItem = audioItem,
                            mediaViewModel = mediaViewModel,
                            downloadDir = downloadDir,
                            backgroundColor = lavenderPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioItem(
    audioItem: AudioModelItem,
    mediaViewModel: MediaViewModel,
    downloadDir: File,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor.copy(alpha = 0.1f))
            .clickable(onClick = {  })
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork
        Image(
            painter = rememberAsyncImagePainter(audioItem.songBanner),
            contentDescription = audioItem.songName,
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
                text = audioItem.songName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Text(
                text = audioItem.singer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // Download Button
        IconButton(
            onClick = { /* Download logic */ },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = Color.Black
            )
        }
    }
}