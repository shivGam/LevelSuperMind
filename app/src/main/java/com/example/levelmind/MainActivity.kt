package com.example.levelmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.levelmind.api.RetrofitInstance
import com.example.levelmind.navigation.BottomNav
import com.example.levelmind.repositories.MediaRepository
import com.example.levelmind.ui.theme.LevelMindTheme
import com.example.levelmind.viewmodals.MediaViewModel
import com.example.levelmind.viewmodals.MediaViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mediaViewModel: MediaViewModel by viewModels{
            MediaViewModelFactory(MediaRepository(RetrofitInstance.api))
        }
        setContent {
            LevelMindTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ){

                    val navController = rememberNavController()
                    BottomNav(navController = navController,mediaViewModel = mediaViewModel)
                }
            }
        }
    }
}
