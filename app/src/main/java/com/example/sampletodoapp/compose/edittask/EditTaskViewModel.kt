package com.example.sampletodoapp.compose.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId = checkNotNull(savedStateHandle.get<Int>("taskId"))

    val taskFlow: StateFlow<Task?> = tasksRepository.loadTaskById(taskId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    // タスクの更新
    fun updateTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.updateTask(task)
        }
    }
}