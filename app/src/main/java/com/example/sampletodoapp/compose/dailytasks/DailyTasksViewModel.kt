package com.example.sampletodoapp.compose.dailytasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DailyTasksUiState {
    object Loading : DailyTasksUiState

    data class Success(
        val groupedTasks: Map<String, List<Task>>, // deadlineごとのタスク
        val isEmpty: Boolean
    ) : DailyTasksUiState

    // エラー状態
    data class Error(val message: String) : DailyTasksUiState
}

@HiltViewModel
class DailyTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DailyTasksUiState>(DailyTasksUiState.Loading)
    val uiState: StateFlow<DailyTasksUiState> = _uiState

    init {
        // ViewModelの初期化時にタスクをロード
        loadTasks()
    }

    // タスクのロード
    private fun loadTasks() {
        viewModelScope.launch {
            tasksRepository.getUnfinishedTasks()
                .collect { tasks ->
                    if (tasks.isEmpty()) {
                        _uiState.value = DailyTasksUiState.Success(emptyMap(), true)
                    } else {
                        val grouped = tasks.groupBy { it.deadline }.toSortedMap()
                        _uiState.value = DailyTasksUiState.Success(grouped, false)
                    }
                }
        }
    }

    // タスクの削除
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.deleteTask(task)
        }
    }

    // タスクの完了
    fun switchTask(task: Task) {
        viewModelScope.launch {
            val newIsDone = !task.isDone
            tasksRepository.updateTask(
                task.copy(
                    isDone = newIsDone,
                    progress = if (newIsDone) 100 else 0
                )
            )
        }
    }
}