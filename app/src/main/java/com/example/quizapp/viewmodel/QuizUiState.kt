package com.example.quizapp.viewmodel

import com.example.quizapp.data.model.Difficulty
import com.example.quizapp.data.model.Question

/**
 * Immutable UI State representing the Quiz lifecycle and active state.
 */
data class QuizUiState(
    val difficulty: Difficulty? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val selectedAnswer: String? = null,
    val isFinished: Boolean = false,
    val bestScore: Int = 0,
    val isNewBestScore: Boolean = false
) {
    val totalQuestions: Int
        get() = questions.size

    val currentQuestionNumber: Int
        get() = currentQuestionIndex + 1

    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val progress: Float
        get() = if (totalQuestions > 0) (currentQuestionNumber.toFloat() / totalQuestions) else 0f

    val progressPercentage: Int
        get() = (progress * 100).toInt()
}
