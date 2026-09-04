package com.example.quizapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.difficulty.DifficultyScreen
import com.example.quizapp.ui.home.HomeScreen
import com.example.quizapp.ui.quiz.QuizScreen
import com.example.quizapp.ui.result.ResultScreen
import com.example.quizapp.viewmodel.QuizViewModel

@Composable
fun QuizNavHost(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reactively navigate to Result screen when quiz finishes
    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished && navController.currentDestination?.route == Screen.Quiz.route) {
            navController.navigate(Screen.Result.route) {
                popUpTo(Screen.Quiz.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                bestScore = uiState.bestScore,
                onStartQuizClick = {
                    navController.navigate(Screen.Difficulty.route)
                }
            )
        }

        composable(Screen.Difficulty.route) {
            DifficultyScreen(
                onDifficultySelected = { difficulty ->
                    viewModel.selectDifficulty(difficulty)
                    navController.navigate(Screen.Quiz.route)
                },
                onBackToHomeClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                uiState = uiState,
                onAnswerSelected = { answer ->
                    viewModel.answerQuestion(answer)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                uiState = uiState,
                onPlayAgainClick = {
                    viewModel.playAgain()
                    navController.navigate(Screen.Difficulty.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onBackToHomeClick = {
                    viewModel.backToHome()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
