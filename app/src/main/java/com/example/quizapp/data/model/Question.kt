package com.example.quizapp.data.model

/**
 * Model representing a single quiz question.
 *
 * Requirements:
 * - Exactly 4 options
 * - Only 1 correct answer
 * - Correct answer must be contained in [options]
 * - No duplicate options allowed
 */
data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
) {
    init {
        require(options.size == 4) { "Question must contain exactly 4 options" }
        require(options.distinct().size == 4) { "Question options must not contain duplicates" }
        require(options.contains(correctAnswer)) { "Options must contain the correct answer" }
    }
}
