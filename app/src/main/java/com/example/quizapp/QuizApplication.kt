package com.example.quizapp

import android.app.Application
import com.example.quizapp.data.repository.DataStoreScoreRepository
import com.example.quizapp.data.repository.ScoreRepository

class QuizApplication : Application() {

    lateinit var scoreRepository: ScoreRepository
        private set

    override fun onCreate() {
        super.onCreate()
        scoreRepository = DataStoreScoreRepository(this)
    }
}
