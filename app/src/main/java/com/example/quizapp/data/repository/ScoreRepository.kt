package com.example.quizapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extension property for DataStore singleton
val Context.quizDataStore: DataStore<Preferences> by preferencesDataStore(name = "quiz_preferences")

/**
 * Repository interface for managing persistent quiz score.
 */
interface ScoreRepository {
    val bestScoreFlow: Flow<Int>
    suspend fun getBestScore(): Int
    suspend fun saveBestScore(score: Int)
}

/**
 * Production implementation of [ScoreRepository] backed by Jetpack DataStore Preferences.
 */
class DataStoreScoreRepository(
    private val dataStore: DataStore<Preferences>
) : ScoreRepository {

    companion object {
        val BEST_SCORE_KEY = intPreferencesKey("best_score_key")
    }

    constructor(context: Context) : this(context.quizDataStore)

    override val bestScoreFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[BEST_SCORE_KEY] ?: 0
        }

    override suspend fun getBestScore(): Int {
        return bestScoreFlow.first()
    }

    override suspend fun saveBestScore(score: Int) {
        dataStore.edit { preferences ->
            val currentBest = preferences[BEST_SCORE_KEY] ?: 0
            if (score > currentBest) {
                preferences[BEST_SCORE_KEY] = score
            }
        }
    }
}
