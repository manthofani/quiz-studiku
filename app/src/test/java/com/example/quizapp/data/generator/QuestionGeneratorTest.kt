package com.example.quizapp.data.generator

import com.example.quizapp.data.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class QuestionGeneratorTest {

    private lateinit var generator: MathQuestionGenerator

    @Before
    fun setUp() {
        // Use a fixed seed for reproducible assertions
        generator = MathQuestionGenerator(Random(42))
    }

    @Test
    fun generateEasyQuestion_shouldReturnFourOptions() {
        val question = generator.generate(Difficulty.EASY)
        assertNotNull(question)
        assertEquals(4, question.options.size)
    }

    @Test
    fun generateMediumQuestion_shouldReturnFourOptions() {
        val question = generator.generate(Difficulty.MEDIUM)
        assertNotNull(question)
        assertEquals(4, question.options.size)
    }

    @Test
    fun generateHardQuestion_shouldReturnFourOptions() {
        val question = generator.generate(Difficulty.HARD)
        assertNotNull(question)
        assertEquals(4, question.options.size)
    }

    @Test
    fun generatedQuestion_shouldContainCorrectAnswer() {
        for (difficulty in Difficulty.entries) {
            repeat(10) {
                val question = generator.generate(difficulty)
                assertTrue(
                    "Options ${question.options} must contain correct answer ${question.correctAnswer}",
                    question.options.contains(question.correctAnswer)
                )
            }
        }
    }

    @Test
    fun generatedQuestion_shouldNotContainDuplicateOptions() {
        for (difficulty in Difficulty.entries) {
            repeat(15) {
                val question = generator.generate(difficulty)
                val uniqueCount = question.options.distinct().size
                assertEquals(
                    "Options must not have duplicates for difficulty $difficulty: ${question.options}",
                    4,
                    uniqueCount
                )
            }
        }
    }

    @Test
    fun generateQuiz_shouldReturnTenQuestions() {
        val quiz = generator.generateQuiz(Difficulty.MEDIUM, count = 10)
        assertEquals(10, quiz.size)
        quiz.forEach { q ->
            assertEquals(4, q.options.size)
            assertTrue(q.options.contains(q.correctAnswer))
        }
    }

    @Test
    fun difficulty_shouldProduceDifferentQuestionCharacteristics() {
        // Generate a sample of questions per difficulty
        val easyQuestions = (1..20).map { generator.generate(Difficulty.EASY).question }
        val hardQuestions = (1..20).map { generator.generate(Difficulty.HARD).question }

        // Hard questions include %, ², or algebraic equations (x)
        val hardHasAdvancedPatterns = hardQuestions.any {
            it.contains("%") || it.contains("²") || it.contains("x")
        }
        assertTrue("Hard questions should contain advanced mathematical symbols", hardHasAdvancedPatterns)

        // Easy questions do not contain advanced patterns like % or ²
        val easyHasAdvancedPatterns = easyQuestions.any {
            it.contains("%") || it.contains("²") || it.contains("Solve for x")
        }
        assertFalse("Easy questions should not contain advanced mathematical symbols", easyHasAdvancedPatterns)
    }
}
