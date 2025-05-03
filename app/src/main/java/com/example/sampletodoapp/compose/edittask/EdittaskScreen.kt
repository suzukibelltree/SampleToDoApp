package com.example.sampletodoapp.compose.edittask

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun EditTaskScreen(
    viewModel: EditTaskViewModel = hiltViewModel(),
    navController: NavController,
) {
    val task = viewModel.taskFlow.collectAsState()
    task.value?.let {
        Text(
            text = it.title
        )
    }
}