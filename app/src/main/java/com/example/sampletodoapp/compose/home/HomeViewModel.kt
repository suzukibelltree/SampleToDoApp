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

// ホーム画面のUI状態
sealed interface HomeUiState {
    // タスクの読み込み中状態
    data object Loading : HomeUiState

    // タスクの読み込み成功状態
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
        tasksRepository.getFinishedTasks(), // 2つのFlowのどちらかに変更があったら新しい値を生成
        tasksRepository.getUnfinishedTasks()
    ) { finishedTasks, unfinishedTasks ->
        // ホーム画面のUI状態を変更
        HomeUiState.Success(
            finishedTasks = finishedTasks,
            unfinishedTasks = unfinishedTasks,
            isEmpty = (finishedTasks.isEmpty() && unfinishedTasks.isEmpty())
        )
    }.stateIn( // FlowをStateFlowに変換することにより、コンポーザブル関数側で監視可能にする
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
}