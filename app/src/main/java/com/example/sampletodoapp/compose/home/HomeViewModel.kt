package com.example.sampletodoapp.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ホーム画面のUI状態
sealed interface HomeUiState {
    // タスクの読み込み中状態
    data object Loading : HomeUiState

    // タスクの読み込み成功状態
    data class Success(
        val finishedTasks: List<Task>,
        val unfinishedTasks: List<Task>,
        val selectedPriority: TaskPriority? = null, // 選択された優先度
        val isEmpty: Boolean = false
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _selectedPriority = MutableStateFlow<TaskPriority?>(null)
    val selectedPriority: StateFlow<TaskPriority?> = _selectedPriority

    val uiState: StateFlow<HomeUiState> = combine(
        tasksRepository.getFinishedTasks(), // 2つのFlowのどちらかに変更があったら新しい値を生成
        tasksRepository.getUnfinishedTasks(),
        _selectedPriority
    ) { finishedTasks, unfinishedTasks, priority ->
        val filteredFinishedTasks = finishedTasks.filter { task ->
            priority == null || TaskPriority.fromLevel(task.importance) == priority
        }
        val filteredUnfinishedTasks = unfinishedTasks.filter { task ->
            priority == null || TaskPriority.fromLevel(task.importance) == priority
        }
        // ホーム画面のUI状態を変更
        HomeUiState.Success(
            finishedTasks = filteredFinishedTasks,
            unfinishedTasks = filteredUnfinishedTasks,
            selectedPriority = priority,
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
            val newIsDone = !task.isDone
            tasksRepository.updateTask(
                task.copy(
                    isDone = newIsDone,
                    progress = if (newIsDone) 100 else 0
                )
            )
        }
    }

    // タスクのフィルタリング
    fun filterTasks(priority: TaskPriority?) {
        _selectedPriority.value = priority
    }
}