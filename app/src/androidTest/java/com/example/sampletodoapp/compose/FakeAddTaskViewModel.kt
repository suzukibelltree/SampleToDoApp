package com.example.sampletodoapp.compose

import com.example.sampletodoapp.compose.addtask.AddTaskUiState
import com.example.sampletodoapp.compose.addtask.AddTaskViewModel
import com.example.sampletodoapp.room.FakeTasksRepository
import com.example.sampletodoapp.room.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAddTaskViewModel : AddTaskViewModel(FakeTasksRepository()) {

    override val uiState = MutableStateFlow(
        AddTaskUiState.Input(
            title = "",
            deadline = "",
            importance = TaskPriority.MEDIUM,
            color = 0xFFFFFF
        )
    )
}

