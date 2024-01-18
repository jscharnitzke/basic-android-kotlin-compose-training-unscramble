package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    val uiState: StateFlow<GameUiState>
        get() = _uiState.asStateFlow()

    init {
        resetGame()
    }

    fun checkUserGuess() {
        if(userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update {currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }

        updateUserGuess("")
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()

        if(usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    private fun shuffleCurrentWord(word: String): String {
        val chars = word.toCharArray()

        chars.shuffle()
        while(String(chars) == word) {
            chars.shuffle()
        }

        return String(chars)
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        _uiState.update {currentState ->
            currentState.copy(
                currentScrambledWord = pickRandomWordAndShuffle(),
                currentWordCount = currentState.currentWordCount.inc(),
                isGuessedWordWrong = false,
                score = updatedScore
            )
        }
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }
}