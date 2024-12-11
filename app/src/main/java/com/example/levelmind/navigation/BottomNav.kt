package com.example.levelmind.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.levelmind.data.BottomNavParams
import com.example.levelmind.screens.Downloads
import com.example.levelmind.screens.Media
import com.example.levelmind.viewmodals.MediaViewModel

@Composable
fun BottomNav(navController: NavHostController,mediaViewModel : MediaViewModel){
    val navHostController = rememberNavController()

    Scaffold(bottomBar = { FloatingBottomBar(navHostController) }) { innerPadding ->
        NavHost(navController = navHostController, startDestination = Routes.Media.routes, modifier = Modifier.padding(innerPadding) ){
            composable(Routes.Media.routes){
                Media(mediaViewModel)
            }
            composable(Routes.Downloads.routes){
                Downloads()
            }
        }
    }
}
@Composable
fun FloatingBottomBar(navHostController: NavHostController) {
    val backStackEntry = navHostController.currentBackStackEntryAsState()
    val navItems = listOf(
        BottomNavParams(
            "Media",
            Routes.Media.routes,
            Icons.Rounded.MusicNote
        ),
        BottomNavParams(
            "Downloads",
            Routes.Downloads.routes,
            Icons.Rounded.DownloadDone
        )
    )
    BottomAppBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
            navItems.forEach {
                val selected = it.route == backStackEntry.value?.destination?.route
                NavigationBarItem(selected = selected, onClick = {
                    navHostController.navigate(it.route){
                        popUpTo(navHostController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }, icon = {
                    Icon(imageVector = it.icon , contentDescription = it.title)
                }, label = {
                    if(selected) Text(text = it.title)
                }, alwaysShowLabel = true)
            }
    }
}


