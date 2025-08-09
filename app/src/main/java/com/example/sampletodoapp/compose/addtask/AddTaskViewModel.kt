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
    // タスクの入力状態
    data class Input(
        val title: String = "",
        val deadline: String = "",
        val importance: TaskPriority = TaskPriority.MEDIUM,
        val color: Long = 0xFFd3d3d3 // デフォルトの色を設定
    ) : AddTaskUiState

    // タスクの保存中状態
    data object Saving : AddTaskUiState

    // タスクの保存成功状態
    data object Success : AddTaskUiState
}

@HiltViewModel
open class AddTaskViewModel @Inject constructor(
    private val taskRepository: TasksRepository
) :
    ViewModel() {
    // 内部保存用
    private val _uiState = MutableStateFlow<AddTaskUiState>(AddTaskUiState.Input())

    // 外部公開用(外部からは変更できない)
    open val uiState: StateFlow<AddTaskUiState> = _uiState

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

    fun updateColor(newColor: Long) {
        val current = _uiState.value
        if (current is AddTaskUiState.Input) {
            _uiState.value = current.copy(color = newColor)
        }
    }

    // タスクの保存
    // 処理の中でUI状態を変更している
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