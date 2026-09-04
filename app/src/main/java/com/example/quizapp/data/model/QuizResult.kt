package com.example.quizapp.data.model

/**
 * Summary data class for the completed quiz round.
 */
data class QuizResult(
    val difficulty: Difficulty,
    val score: Int,
    val totalQuestions: Int = 10,
    val isNewBestScore: Boolean = false
) {
    val scorePercentage: Int
        get() = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
}
