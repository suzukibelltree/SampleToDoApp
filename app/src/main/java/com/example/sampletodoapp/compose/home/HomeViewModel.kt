package com.example.sampletodoapp.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val finishedTasks: List<Task>,
        val unfinishedTasks: List<Task>,
        val isEmpty: Boolean = false
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        tasksRepository.getFinishedTasks(),
        tasksRepository.getUnfinishedTasks()
    ) { finishedTasks, unfinishedTasks ->
        HomeUiState.Success(
            finishedTasks = finishedTasks,
            unfinishedTasks = unfinishedTasks,
            isEmpty = (finishedTasks.isEmpty() && unfinishedTasks.isEmpty())
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

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