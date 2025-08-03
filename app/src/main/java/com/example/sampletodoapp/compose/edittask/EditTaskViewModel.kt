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

/// タスク編集画面のUI状態
sealed interface EditTaskUiState {
    // タスクの編集状態
    data class Edit(
        val id: Int,
        val title: String,
        val deadline: String,
        val importance: TaskPriority,
        val color: Long,
        val progress: Int,
        val isDone: Boolean
    ) : EditTaskUiState

    // タスクのロード中状態
    data object Loading : EditTaskUiState

    // タスクの保存成功状態
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

    // ViewModelの初期化処理
    init {
        viewModelScope.launch {
            val task = tasksRepository.loadTaskById(taskId).first()
            // UI状態をLoadingからEditに変更している
            _uiState.value = EditTaskUiState.Edit(
                id = task.id,
                title = task.title,
                deadline = task.deadline,
                importance = TaskPriority.fromLevel(task.importance),
                isDone = task.isDone,
                progress = task.progress,
                color = task.color
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

    fun updateColor(newColor: Long) {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            _uiState.value = current.copy(color = newColor)
        }
    }

    // タスクの進捗度の更新
    fun updateProgress(newProgress: Int) {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            _uiState.value = current.copy(
                progress = newProgress,
                isDone = (newProgress == 100)
            )
        }
    }

    // タスクの完了状態の更新
    fun toggleIsDone() {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            val newIsDone = !current.isDone
            _uiState.value = current.copy(
                isDone = newIsDone,
                progress = if (newIsDone) 100 else 0
            )
        }
    }

    // タスクの更新
    // 処理の中でUI状態を変更している
    fun saveTask() {
        val current = _uiState.value
        if (current is EditTaskUiState.Edit) {
            viewModelScope.launch {
                val updatedTask = Task(
                    id = current.id,
                    title = current.title,
                    deadline = current.deadline,
                    importance = current.importance.level,
                    color = current.color,
                    progress = current.progress,
                    isDone = current.isDone
                )
                tasksRepository.updateTask(updatedTask)
                _uiState.value = EditTaskUiState.Success
            }
        }
    }
}