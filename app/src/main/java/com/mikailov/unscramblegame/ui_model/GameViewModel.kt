package com.mikailov.unscramblegame.ui_model

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.mikailov.unscramblegame.data.GameUiState
import com.mikailov.unscramblegame.data.MAX_NO_OF_WORDS
import com.mikailov.unscramblegame.data.SCORE_INCREASE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.mikailov.unscramblegame.data.allWords

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle()
        )
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {

            val updatedScore = _uiState.value.score + SCORE_INCREASE
            updateGameState(updatedScore)
        } else {

            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }

        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {

            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {

            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount + 1
                )
            }
        }
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Сбрасываем текст пользователя
        updateUserGuess("")
    }

    private fun pickRandomWordAndShuffle(): String {
        // Выбираем случайное слово, которое ещё не использовали
        currentWord = allWords.random()
        // Если слово уже было использовано, выбираем другое
        while (usedWords.contains(currentWord)) {
            currentWord = allWords.random()
        }
        // Добавляем слово в список использованных
        usedWords.add(currentWord)
        // Перемешиваем буквы в слове
        return shuffleCurrentWord(currentWord)
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()

        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }
}