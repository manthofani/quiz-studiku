package com.example.quizapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeScoreRepository(initialScore: Int = 0) : ScoreRepository {

    private val _bestScoreFlow = MutableStateFlow(initialScore)
    override val bestScoreFlow: Flow<Int> = _bestScoreFlow.asStateFlow()

    override suspend fun getBestScore(): Int {
        return _bestScoreFlow.value
    }

    override suspend fun saveBestScore(score: Int) {
        if (score > _bestScoreFlow.value) {
            _bestScoreFlow.value = score
        }
    }
}
