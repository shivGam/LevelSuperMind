package com.example.levelmind.navigation

sealed class Routes(val routes: String){
    data object Media : Routes("media")
    data object Downloads : Routes("downloads")
}