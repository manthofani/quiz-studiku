package com.example.quizapp.viewmodel

import com.example.quizapp.data.generator.MathQuestionGenerator
import com.example.quizapp.data.model.Difficulty
import com.example.quizapp.data.repository.FakeScoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeScoreRepository
    private lateinit var generator: MathQuestionGenerator
    private lateinit var viewModel: QuizViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeScoreRepository(initialScore = 5)
        generator = MathQuestionGenerator(Random(1234))
        viewModel = QuizViewModel(
            scoreRepository = fakeRepository,
            questionGenerator = generator
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectDifficulty_initializesTenQuestionsAndResetsState() {
        viewModel.selectDifficulty(Difficulty.EASY)
        val state = viewModel.uiState.value

        assertEquals(Difficulty.EASY, state.difficulty)
        assertEquals(10, state.questions.size)
        assertEquals(0, state.currentQuestionIndex)
        assertEquals(0, state.score)
        assertFalse(state.isFinished)
    }

    @Test
    fun correctAnswer_shouldIncrementScore() {
        viewModel.selectDifficulty(Difficulty.EASY)
        val firstQuestion = viewModel.uiState.value.currentQuestion!!
        val correctAnswer = firstQuestion.correctAnswer

        viewModel.answerQuestion(correctAnswer)

        val state = viewModel.uiState.value
        assertEquals("Score should increment by 1 on correct answer", 1, state.score)
        assertEquals("Should advance to next question", 1, state.currentQuestionIndex)
    }

    @Test
    fun wrongAnswer_shouldNotIncrementScore() {
        viewModel.selectDifficulty(Difficulty.EASY)
        val firstQuestion = viewModel.uiState.value.currentQuestion!!
        // Pick an option that is definitely not correct
        val wrongAnswer = firstQuestion.options.first { it != firstQuestion.correctAnswer }

        viewModel.answerQuestion(wrongAnswer)

        val state = viewModel.uiState.value
        assertEquals("Score should remain 0 on wrong answer", 0, state.score)
        assertEquals("Should advance to next question", 1, state.currentQuestionIndex)
    }

    @Test
    fun quizFinishes_afterTenQuestions_andScoreDoesNotExceedTen() = runTest {
        viewModel.selectDifficulty(Difficulty.MEDIUM)

        repeat(10) {
            val currentQ = viewModel.uiState.value.currentQuestion!!
            viewModel.answerQuestion(currentQ.correctAnswer)
        }

        val state = viewModel.uiState.value
        assertTrue("Quiz should be marked as finished after 10 questions", state.isFinished)
        assertEquals("Score should equal 10 when all 10 are correct", 10, state.score)
        assertTrue("Score must not exceed total questions (10)", state.score <= 10)
    }

    @Test
    fun higherScore_shouldUpdateBestScore() = runTest {
        // Initial best score in fakeRepository is 5
        viewModel.selectDifficulty(Difficulty.HARD)

        // Answer 8 questions correctly, 2 wrong => total score 8 (> 5)
        repeat(8) {
            val q = viewModel.uiState.value.currentQuestion!!
            viewModel.answerQuestion(q.correctAnswer)
        }
        repeat(2) {
            val q = viewModel.uiState.value.currentQuestion!!
            val wrong = q.options.first { it != q.correctAnswer }
            viewModel.answerQuestion(wrong)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val finalScore = viewModel.uiState.value.score
        assertEquals(8, finalScore)
        assertEquals(8, fakeRepository.getBestScore())
    }

    @Test
    fun lowerScore_shouldNotOverwriteHigherBestScore() = runTest {
        // Initial best score in fakeRepository is 5
        viewModel.selectDifficulty(Difficulty.EASY)

        // Answer only 3 questions correctly, 7 wrong => total score 3 (< 5)
        repeat(3) {
            val q = viewModel.uiState.value.currentQuestion!!
            viewModel.answerQuestion(q.correctAnswer)
        }
        repeat(7) {
            val q = viewModel.uiState.value.currentQuestion!!
            val wrong = q.options.first { it != q.correctAnswer }
            viewModel.answerQuestion(wrong)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val finalScore = viewModel.uiState.value.score
        assertEquals(3, finalScore)
        assertEquals("Best score should remain 5 when new score is lower", 5, fakeRepository.getBestScore())
    }
}
