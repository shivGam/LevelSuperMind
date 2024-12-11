package com.example.levelmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.levelmind.navigation.BottomNav
import com.example.levelmind.ui.theme.LevelMindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelMindTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ){
                    val navController = rememberNavController()
                    BottomNav(navController = navController)
                }
            }
        }
    }
}
