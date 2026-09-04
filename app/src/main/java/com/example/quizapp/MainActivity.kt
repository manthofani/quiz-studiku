package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.quizapp.data.generator.MathQuestionGenerator
import com.example.quizapp.ui.navigation.QuizNavHost
import com.example.quizapp.ui.theme.QuizAppTheme
import com.example.quizapp.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels {
        val app = application as QuizApplication
        QuizViewModel.Factory(
            scoreRepository = app.scoreRepository,
            questionGenerator = MathQuestionGenerator()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizNavHost(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
