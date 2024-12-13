package com.example.levelmind.screens

import MediaPlayBackOverlay
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.rememberAsyncImagePainter
import com.example.levelmind.R
import com.example.levelmind.data.models.AudioModelItem
import com.example.levelmind.utils.DownloadWorker
import com.example.levelmind.viewmodals.MediaViewModel
import kotlinx.coroutines.delay
import java.io.File
@Composable
fun Media(mediaViewModel: MediaViewModel) {
    val audioList by mediaViewModel.audioList.observeAsState(emptyList())
    val context = LocalContext.current
    val downloadDir = context.filesDir

    val listState = rememberLazyListState()

    val lavenderBackground = Color(0xFFF0E6FF)
    val lavenderPrimary = Color(0xFF9E7BFF)
    val lavenderText = Color(0xFF5E35B1)

    // State to manage currently selected audio for playback
    var selectedAudio by remember { mutableStateOf<AudioModelItem?>(null) }
    var isInternetAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            isInternetAvailable = isNetworkAvailable(context)
            delay(2000) // I don't prefer this method, but this is a small assignment
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lavenderBackground)
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.banner),
                        contentDescription = "Top Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

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

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            Text(
                                text = "Enjoy Music",
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = lavenderText
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Discover Your Favorite Sounds",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = lavenderText.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }
            if (audioList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if(isInternetAvailable){
                            CircularProgressIndicator(
                                color = lavenderPrimary,
                                modifier = Modifier.size(50.dp)
                            )
                        }else{
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No Internet Connection",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            } else {
                items(audioList) { audioItem ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AudioItem(
                            audioItem = audioItem,
                            mediaViewModel = mediaViewModel,
                            downloadDir = downloadDir,
                            context = context,
                            backgroundColor = lavenderPrimary,
                            onAudioSelected = {
                                // Only set selected audio if internet is available
                                if (isNetworkAvailable(context)) {
                                    selectedAudio = it
                                } else {
                                    // Optionally show a toast or snackbar about no internet connection
                                    Toast.makeText(
                                        context,
                                        "No internet connection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        if (isNetworkAvailable(context)) {
            selectedAudio?.let { audio ->
                MediaPlayBackOverlay(
                    audioItem = audio,
                    mediaViewModel = mediaViewModel,
                    onClose = { selectedAudio = null }
                )
            }
        }
    }
}

@Composable
fun AudioItem(
    audioItem: AudioModelItem,
    mediaViewModel: MediaViewModel,
    downloadDir: File,
    context: Context,
    backgroundColor: Color,
    onAudioSelected: (AudioModelItem) -> Unit
) {
    val downloadedAudioList by mediaViewModel.downloadedAudioList.observeAsState(emptyList())

    // Check if the current audio is in the downloaded list
    val isDownloaded = downloadedAudioList.any { it.id == audioItem._id }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor.copy(alpha = 0.1f))
            .clickable { onAudioSelected(audioItem) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(audioItem.songBanner),
            contentDescription = audioItem.songName,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

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

        IconButton(
            onClick = {
                if (!isDownloaded) {
                    val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                        .setInputData(
                            workDataOf(
                                "download_url" to audioItem.url,
                                "song_id" to audioItem._id
                            )
                        )
                        .build()

                    WorkManager.getInstance(context).enqueue(downloadRequest)
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            if (isDownloaded) {
                // Display checkmark icon if the audio is downloaded
                Icon(
                    imageVector = Icons.Default.DownloadDone,
                    contentDescription = "Downloaded",
                    tint = Color.Black
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = Color.Black
                )
            }
        }
    }
}
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as? ConnectivityManager ?: return false

    return try {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } catch (e: Exception) {
        // Log the exception if needed
        false
    }
}