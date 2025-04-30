package com.example.sampletodoapp.compose.addtask

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface AddTaskUiState {
    data class Input(
        val title: String = "",
        val deadline: String = "",
        val importance: String = "",
    ) : AddTaskUiState

    data object Saving : AddTaskUiState

    data object Success : AddTaskUiState
}

class AddTaskViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow<AddTaskUiState>(AddTaskUiState.Input())
    val uiState: StateFlow<AddTaskUiState> = _uiState

    // タスクのタイトルの更新
    fun updateTitle(newTitle: String) {
        val currentTitle = _uiState.value
        if (currentTitle is AddTaskUiState.Input) {
            _uiState.value = currentTitle.copy(title = newTitle)
        }
    }

    // タスクの期限日の更新
    fun updateDeadline(newDeadline: String) {
        val current = _uiState.value
        if (current is AddTaskUiState.Input) {
            _uiState.value = current.copy(deadline = newDeadline)
        }
    }

    // タスクの重要度の更新
    fun updateImportance(newImportance: String) {
        val current = _uiState.value
        if (current is AddTaskUiState.Input) {
            _uiState.value = current.copy(importance = newImportance)
        }
    }
}