import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.levelmind.data.models.AudioModelItem
import com.example.levelmind.viewmodals.MediaViewModel

@Composable
fun MediaPlayBackOverlay(
    audioItem: AudioModelItem,
    mediaViewModel: MediaViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lavenderBackground = Color(0xFFF0E6FF)
    val lavenderPrimary = Color(0xFF9E7BFF)

    // MediaPlayer state
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    var totalDuration by remember { mutableStateOf(0f) }

    // Dismissal state
    var dismissProgress by remember { mutableStateOf(0f) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Update MediaPlayer when audioItem changes
    DisposableEffect(audioItem) {
        // Initialize MediaPlayer
        val player = MediaPlayer().apply {
            try {
                setDataSource(context, Uri.parse(audioItem.url))
                prepare()
                totalDuration = duration.toFloat()
            } catch (e: Exception) {
                Log.e("MediaPlayBackOverlay", "Error setting audio source: ${e.message}")
            }
        }
        mediaPlayer = player

        // Set up progress tracking
        val handler = Handler(Looper.getMainLooper())
        val updateProgress = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    currentPosition = player.currentPosition.toFloat()
                    handler.postDelayed(this, 100)
                }
            }
        }

        // Playback completion listener
        player.setOnCompletionListener {
            isPlaying = false
            currentPosition = totalDuration
        }

        // Cleanup
        onDispose {
            handler.removeCallbacks(updateProgress)
            player.release()
        }
    }

    // Animated opacity for dismiss
    val opacity by animateFloatAsState(
        targetValue = 1f - dismissProgress,
        label = "Overlay Opacity"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f * opacity))
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        dismissProgress = (dragAmount / screenHeight.toPx()).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        if (dismissProgress > 0.3f) {
                            onClose()
                        } else {
                            dismissProgress = 0f
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(350.dp)
                .padding(16.dp)
                .offset(y = (screenHeight * dismissProgress))
                .clip(RoundedCornerShape(20.dp))
                .background(lavenderBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Player",
                        tint = Color.Black
                    )
                }
            }

            // Song Banner
            AsyncImage(
                model = audioItem.songBanner,
                contentDescription = audioItem.songName,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            // Song Details
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = audioItem.songName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Text(
                text = audioItem.singer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // Playback Slider
            Spacer(modifier = Modifier.height(16.dp))
            Slider(
                value = currentPosition,
                onValueChange = {
                    currentPosition = it
                    mediaPlayer?.seekTo(it.toInt())
                },
                valueRange = 0f..totalDuration,
                colors = SliderDefaults.colors(
                    thumbColor = lavenderPrimary,
                    activeTrackColor = lavenderPrimary,
                    inactiveTrackColor = lavenderPrimary.copy(alpha = 0.3f)
                )
            )

            // Time Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition.toInt() / 1000),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = formatTime(totalDuration.toInt() / 1000),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Playback Controls
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        mediaPlayer?.let { player ->
                            val newPosition = maxOf(0, player.currentPosition - 10000)
                            player.seekTo(newPosition)
                            currentPosition = newPosition.toFloat()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10 seconds",
                        modifier = Modifier.size(36.dp),
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = {
                        mediaPlayer?.let { player ->
                            if (isPlaying) {
                                player.pause()
                                isPlaying = false
                            } else {
                                player.start()
                                isPlaying = true
                            }
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(lavenderPrimary, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        mediaPlayer?.let { player ->
                            val newPosition = minOf(totalDuration, player.currentPosition + 10000f)
                            player.seekTo(newPosition.toInt())
                            currentPosition = newPosition
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10 seconds",
                        modifier = Modifier.size(36.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

// Utility function to format time (remains the same as before)
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}