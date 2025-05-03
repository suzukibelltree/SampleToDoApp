package com.example.sampletodoapp.compose.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddTaskUiState {
    data class Input(
        val title: String = "",
        val deadline: String = "",
        val importance: TaskPriority = TaskPriority.MEDIUM,
    ) : AddTaskUiState

    data object Saving : AddTaskUiState

    data object Success : AddTaskUiState
}

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TasksRepository
) :
    ViewModel() {
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
    fun updateImportance(newImportance: Int) {
        val current = _uiState.value

        if (current is AddTaskUiState.Input) {
            _uiState.value = current.copy(importance = TaskPriority.fromLevel(newImportance))
        }
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            _uiState.value = AddTaskUiState.Saving
            try {
                taskRepository.insertTask(task)
                _uiState.value = AddTaskUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddTaskUiState.Input()
            }
        }
    }
}