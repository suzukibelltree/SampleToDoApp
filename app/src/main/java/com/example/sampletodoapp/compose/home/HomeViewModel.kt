package com.example.sampletodoapp.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val tasks: List<Task>,
        val isEmpty: Boolean = false
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            delay(3000)
            tasksRepository.getAllTasks().collect { tasks ->
                _uiState.value = if (tasks.isEmpty()) {
                    HomeUiState.Success(tasks, true)
                } else {
                    HomeUiState.Success(tasks)
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
            tasksRepository.updateTask(task.copy(isDone = !task.isDone))
        }
    }
}