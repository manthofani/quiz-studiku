package com.example.quizapp.data.model

/**
 * Represents the 3 difficulty tiers for the Quiz App.
 * Each difficulty determines the arithmetic complexity and operation types.
 */
enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}
