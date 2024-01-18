package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        assertFalse(currentGameUiState.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ScoreUnchangedAndErrorFlagSet() {
        var currentGameUiState = viewModel.uiState.value
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        assertTrue(currentGameUiState.isGuessedWordWrong)
        assertEquals(0, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        var currentGameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        assertEquals(1, currentGameUiState.currentWordCount)
        assertEquals(0, currentGameUiState.score)
        assertFalse(currentGameUiState.isGuessedWordWrong)
        assertFalse(currentGameUiState.isGameOver)
        assertNotEquals(unscrambledWord, currentGameUiState.currentScrambledWord)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS) {
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            expectedScore += SCORE_INCREASE

            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord((currentGameUiState.currentScrambledWord))
        }

        assertEquals(expectedScore, currentGameUiState.score)
        assertEquals(currentGameUiState.currentWordCount, MAX_NO_OF_WORDS)
        assertTrue(currentGameUiState.isGameOver)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}