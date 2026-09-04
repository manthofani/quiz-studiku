package com.example.quizapp.data.generator

import com.example.quizapp.data.model.Difficulty
import com.example.quizapp.data.model.Question
import kotlin.random.Random

interface QuestionGenerator {
    /**
     * Generates a single question tailored to the specified [difficulty].
     */
    fun generate(difficulty: Difficulty): Question

    /**
     * Generates a list of [count] unique random questions for a quiz session.
     */
    fun generateQuiz(difficulty: Difficulty, count: Int = 10): List<Question>
}

class MathQuestionGenerator(
    private val random: Random = Random.Default
) : QuestionGenerator {

    override fun generate(difficulty: Difficulty): Question {
        return when (difficulty) {
            Difficulty.EASY -> generateEasyQuestion()
            Difficulty.MEDIUM -> generateMediumQuestion()
            Difficulty.HARD -> generateHardQuestion()
        }
    }

    override fun generateQuiz(difficulty: Difficulty, count: Int): List<Question> {
        val questions = mutableListOf<Question>()
        val seenPrompts = mutableSetOf<String>()
        var attempts = 0
        val maxAttempts = count * 10

        while (questions.size < count && attempts < maxAttempts) {
            attempts++
            val q = generate(difficulty)
            if (!seenPrompts.contains(q.question)) {
                seenPrompts.add(q.question)
                questions.add(q)
            }
        }

        // Fallback in case prompt duplication was encountered in small space
        while (questions.size < count) {
            questions.add(generate(difficulty))
        }

        return questions
    }

    private fun generateEasyQuestion(): Question {
        return when (random.nextInt(3)) {
            0 -> {
                // Addition: a + b
                val a = random.nextInt(3, 25)
                val b = random.nextInt(2, 25)
                val correct = a + b
                val prompt = "What is $a + $b?"
                createQuestion(prompt, correct)
            }
            1 -> {
                // Subtraction: a - b (ensuring positive result)
                val b = random.nextInt(2, 20)
                val correct = random.nextInt(3, 25)
                val a = correct + b
                val prompt = "What is $a - $b?"
                createQuestion(prompt, correct)
            }
            else -> {
                // Small multiplication: a * b
                val a = random.nextInt(2, 10)
                val b = random.nextInt(2, 10)
                val correct = a * b
                val prompt = "What is $a × $b?"
                createQuestion(prompt, correct)
            }
        }
    }

    private fun generateMediumQuestion(): Question {
        return when (random.nextInt(3)) {
            0 -> {
                // Larger multiplication
                val a = random.nextInt(11, 21)
                val b = random.nextInt(4, 13)
                val correct = a * b
                val prompt = "What is $a × $b?"
                createQuestion(prompt, correct)
            }
            1 -> {
                // Integer division
                val divisor = random.nextInt(3, 13)
                val quotient = random.nextInt(4, 16)
                val dividend = divisor * quotient
                val prompt = "What is $dividend ÷ $divisor?"
                createQuestion(prompt, quotient)
            }
            else -> {
                // Mixed operation: (a * b) + c or (a * b) - c
                val a = random.nextInt(3, 9)
                val b = random.nextInt(3, 9)
                val c = random.nextInt(4, 20)
                val isAddition = random.nextBoolean()
                val correct = if (isAddition) (a * b) + c else (a * b) - c
                val operator = if (isAddition) "+" else "-"
                val prompt = "What is ($a × $b) $operator $c?"
                createQuestion(prompt, correct)
            }
        }
    }

    private fun generateHardQuestion(): Question {
        return when (random.nextInt(4)) {
            0 -> {
                // Percentage: What is P% of N?
                // Pick clean numbers (10%, 15%, 20%, 25%, 30%, 40%, 50%, 75%)
                val percentages = listOf(10, 15, 20, 25, 30, 40, 50, 75)
                val p = percentages.random(random)
                // Multiples of 20 or 40 so integer result is guaranteed
                val base = random.nextInt(3, 16) * (if (p % 25 == 0) 20 else 40)
                val correct = (p * base) / 100
                val prompt = "What is $p% of $base?"
                createQuestion(prompt, correct)
            }
            1 -> {
                // Powers & Squares: a² - b or a² + b
                val base = random.nextInt(6, 16) // 36 to 225
                val offset = random.nextInt(5, 25)
                val isSubtract = random.nextBoolean()
                val correct = if (isSubtract) (base * base) - offset else (base * base) + offset
                val op = if (isSubtract) "-" else "+"
                val prompt = "What is $base² $op $offset?"
                createQuestion(prompt, correct)
            }
            2 -> {
                // Linear equation: Solve for x: a * x + b = c
                val a = random.nextInt(2, 7)
                val x = random.nextInt(3, 15)
                val isSubtract = random.nextBoolean()
                val b = random.nextInt(3, 20)
                val c = if (isSubtract) (a * x) - b else (a * x) + b
                val op = if (isSubtract) "-" else "+"
                val prompt = "Solve for x: ${a}x $op $b = $c"
                createQuestion(prompt, x)
            }
            else -> {
                // Multi-step combined operations: (a + b) × c - d
                val a = random.nextInt(4, 15)
                val b = random.nextInt(3, 12)
                val c = random.nextInt(2, 6)
                val d = random.nextInt(5, 25)
                val correct = ((a + b) * c) - d
                val prompt = "What is ($a + $b) × $c - $d?"
                createQuestion(prompt, correct)
            }
        }
    }

    /**
     * Creates a [Question] with 4 unique options containing the correct answer,
     * with distractor options generated plausibly around the correct answer.
     */
    private fun createQuestion(prompt: String, correctAnswerValue: Int): Question {
        val correctStr = correctAnswerValue.toString()
        val optionsSet = mutableSetOf(correctStr)

        // Generate plausible distractor candidates
        val offsets = listOf(-3, -2, -1, 1, 2, 3, -5, 5, -10, 10, -4, 4)
            .shuffled(random)

        for (offset in offsets) {
            val distractor = (correctAnswerValue + offset).toString()
            optionsSet.add(distractor)
            if (optionsSet.size == 4) break
        }

        // Safeguard if set did not reach 4
        var multiplier = 1
        while (optionsSet.size < 4) {
            val candidate = (correctAnswerValue + (multiplier * 7)).toString()
            optionsSet.add(candidate)
            multiplier++
        }

        val shuffledOptions = optionsSet.toList().shuffled(random)
        return Question(
            question = prompt,
            options = shuffledOptions,
            correctAnswer = correctStr
        )
    }
}
