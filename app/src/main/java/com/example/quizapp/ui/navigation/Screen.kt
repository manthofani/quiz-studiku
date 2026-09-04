package com.example.quizapp.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Difficulty : Screen("difficulty")
    data object Quiz : Screen("quiz")
    data object Result : Screen("result")
}
