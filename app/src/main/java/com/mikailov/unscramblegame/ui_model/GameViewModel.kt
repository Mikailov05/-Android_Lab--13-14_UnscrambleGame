package com.mikailov.unscramblegame.ui_model

import androidx.lifecycle.ViewModel
import com.mikailov.unscramblegame.data.GameUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.mikailov.unscramblegame.data.allWords

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    // Текущее слово, которое игрок должен угадать (НЕперемешанное)
    private lateinit var currentWord: String
    // Множество уже использованных слов (чтобы не повторяться)
    private var usedWords: MutableSet<String> = mutableSetOf()

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear() // Очищаем список использованных слов
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle()
        )
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
        val tempWord = word.toCharArray() // Преобразуем строку в массив символов
        tempWord.shuffle()
        // Проверяем, что перемешанное слово != исходному
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord) // Преобразуем массив обратно в строку
    }
}