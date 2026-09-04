package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.generator.MathQuestionGenerator
import com.example.quizapp.data.generator.QuestionGenerator
import com.example.quizapp.data.model.Difficulty
import com.example.quizapp.data.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    private val scoreRepository: ScoreRepository,
    private val questionGenerator: QuestionGenerator = MathQuestionGenerator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        // Observe best score continuously from DataStore
        viewModelScope.launch {
            scoreRepository.bestScoreFlow.collectLatest { best ->
                _uiState.update { it.copy(bestScore = best) }
            }
        }
    }

    /**
     * Starts a new quiz session with 10 newly generated questions for [difficulty].
     */
    fun selectDifficulty(difficulty: Difficulty) {
        val newQuestions = questionGenerator.generateQuiz(difficulty, count = 10)
        _uiState.update { current ->
            current.copy(
                difficulty = difficulty,
                questions = newQuestions,
                currentQuestionIndex = 0,
                score = 0,
                selectedAnswer = null,
                isFinished = false,
                isNewBestScore = false
            )
        }
    }

    /**
     * Submits the chosen [answer] for the current question.
     * Evaluates correctness, updates score, and advances to next question or finalizes quiz.
     */
    fun answerQuestion(answer: String) {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        if (currentState.isFinished) return

        val isCorrect = answer.trim() == currentQuestion.correctAnswer.trim()
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        val nextIndex = currentState.currentQuestionIndex + 1
        val isFinishedNow = nextIndex >= currentState.totalQuestions

        if (isFinishedNow) {
            val wasHigher = newScore > currentState.bestScore
            _uiState.update {
                it.copy(
                    score = newScore,
                    selectedAnswer = answer,
                    isFinished = true,
                    isNewBestScore = wasHigher
                )
            }
            if (wasHigher) {
                viewModelScope.launch {
                    scoreRepository.saveBestScore(newScore)
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    score = newScore,
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null
                )
            }
        }
    }

    /**
     * Resets quiz progression so user can pick difficulty again.
     */
    fun playAgain() {
        _uiState.update {
            it.copy(
                questions = emptyList(),
                currentQuestionIndex = 0,
                score = 0,
                selectedAnswer = null,
                isFinished = false,
                isNewBestScore = false
            )
        }
    }

    /**
     * Resets state back to Home screen.
     */
    fun backToHome() {
        _uiState.update {
            it.copy(
                difficulty = null,
                questions = emptyList(),
                currentQuestionIndex = 0,
                score = 0,
                selectedAnswer = null,
                isFinished = false,
                isNewBestScore = false
            )
        }
    }

    /**
     * ViewModel factory for instantiating [QuizViewModel] with dependencies.
     */
    class Factory(
        private val scoreRepository: ScoreRepository,
        private val questionGenerator: QuestionGenerator = MathQuestionGenerator()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                return QuizViewModel(scoreRepository, questionGenerator) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
