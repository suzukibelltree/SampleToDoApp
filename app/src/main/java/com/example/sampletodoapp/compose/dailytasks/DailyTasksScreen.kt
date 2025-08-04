package com.example.sampletodoapp.compose.dailytasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sampletodoapp.room.Task

@Composable
fun DailyTasksScreen(
    viewModel: DailyTasksViewModel = hiltViewModel(),
    onNavigateToEditTask: (taskId: Int) -> Unit,
    modifier: Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    when (val state = uiState.value) {
        is DailyTasksUiState.Loading -> {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                item {
                    CircularProgressIndicator()
                }
            }
        }

        is DailyTasksUiState.Success -> {
            if (state.groupedTasks.isEmpty()) {
                Text(
                    text = "本日のタスクはありません",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                DailyTasksList(
                    groupedTasks = state.groupedTasks,
                    modifier = modifier,
                    onClickTask = { task ->
                        onNavigateToEditTask(task.id)
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task)
                    },
                    onCompleteTask = { task ->
                        viewModel.switchTask(task)
                    }
                )
            }
        }

        is DailyTasksUiState.Error -> {
            // エラーハンドリングのUIをここに実装
        }
    }
}


@Composable
fun DailyTasksList(
    groupedTasks: Map<String, List<Task>>,
    onClickTask: (Task) -> Unit = {},
    onDeleteTask: (Task) -> Unit = {},
    onCompleteTask: (Task) -> Unit = {},
    modifier: Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(16.dp)
    ) {
        groupedTasks.forEach { (deadline, tasksForDay) ->
            item {
                Text(
                    text = "期限日: $deadline",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(tasksForDay.size) { index ->
                val task = tasksForDay[index]
                MiniTaskCard(
                    task = task,
                    onClick = { onClickTask(task) },
                    onDelete = { onDeleteTask(task) },
                    onComplete = { onCompleteTask(task) },
                    color = task.color
                )
            }
        }
    }
}

@Composable
fun MiniTaskCard(
    task: Task,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onComplete: () -> Unit = {},
    color: Long = task.color, // デフォルトはタスクの色
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(color)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (task.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = "Complete",
                modifier = Modifier
                    .clickable { onComplete() }
                    .padding(end = 8.dp)
            )
            Text(text = task.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.clickable { onDelete() }
            )
        }
    }
}
