package com.example.sampletodoapp.compose.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditTaskUiState {
    data class Edit(
        val id: Int,
        val title: String,
        val deadline: String,
        val importance: TaskPriority,
        val isDone: Boolean
    ) : EditTaskUiState

    data object Loading : EditTaskUiState
    data object Success : EditTaskUiState
}

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId = checkNotNull(savedStateHandle.get<Int>("taskId"))

    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Loading)
    val uiState: StateFlow<EditTaskUiState> = _uiState

    init {
        viewModelScope.launch {
            val task = tasksRepository.loadTaskById(taskId).first()
            _uiState.value = EditTaskUiState.Edit(
                id = task.id,
                title = task.title,
                deadline = task.deadline,
                importance = TaskPriority.fromLevel(task.importance),
                isDone = task.isDone
            )
        }
    }

    // タスクのタイトルの更新
    fun updateTitle(newTitle: String) {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            _uiState.value = current.copy(title = newTitle)
        }
    }

    // タスクの期限日の更新
    fun updateDeadline(newDeadline: String) {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            _uiState.value = current.copy(deadline = newDeadline)
        }
    }

    // タスクの重要度の更新
    fun updateImportance(newImportance: Int) {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            _uiState.value = current.copy(importance = TaskPriority.fromLevel(newImportance))
        }
    }

    // タスクの完了状態の更新
    fun toggleIsDone() {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            _uiState.value = current.copy(isDone = !current.isDone)
        }
    }

    // タスクの更新
    fun saveTask() {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            viewModelScope.launch {
                val updatedTask = Task(
                    id = current.id,
                    title = current.title,
                    deadline = current.deadline,
                    importance = current.importance.level,
                    isDone = current.isDone
                )
                tasksRepository.updateTask(updatedTask)
                _uiState.value = EditTaskUiState.Success
            }
        }
    }
}