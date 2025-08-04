package com.example.sampletodoapp.compose.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority

/**
 * タスク一覧画面のUIを表示するComposable関数
 * @param viewModel タスク一覧画面のViewModel
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToEditTask: (taskId: Int) -> Unit,
    modifier: Modifier
) {
    val selectedPriority = viewModel.selectedPriority.collectAsState()
    val state = viewModel.uiState.collectAsState()
    when (val uiState = state.value) {
        is HomeUiState.Loading -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "ロード中...")
            }
        }

        is HomeUiState.Success -> {
            if (uiState.isEmpty) {
                Text(text = "タスクが存在しません")
            } else {
                Column(
                    modifier = modifier.padding(8.dp)
                ) {
                    TaskFilter(
                        selectedFilter = selectedPriority.value,
                        onFilterSelected = { priority ->
                            viewModel.filterTasks(priority)
                        }
                    )
                    TaskListContent(
                        unfinishedTasks = uiState.unfinishedTasks,
                        finishedTasks = uiState.finishedTasks,
                        onClick = { task ->
                            onNavigateToEditTask(task.id)
                        },
                        onDelete = { task ->
                            viewModel.deleteTask(task)
                        },
                        onComplete = { task ->
                            viewModel.switchTask(task)
                        },
                    )
                }
            }
        }
    }
}

/**
 * タスクのフィルタリングUIを表示するComposable関数
 * @param selectedFilter 現在選択されているフィルタ
 * @param onFilterSelected フィルタが選択されたときのコールバック
 */
@Composable
fun TaskFilter(
    selectedFilter: TaskPriority?,
    onFilterSelected: (TaskPriority?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedFilter == null,
            onClick = { onFilterSelected(null) }, // 全てのタスクを表示
            label = { Text("All") },
        )
        FilterChip(
            selected = selectedFilter == TaskPriority.HIGH,
            onClick = { onFilterSelected(TaskPriority.HIGH) },
            label = { Text("重要度:高") },
        )
        FilterChip(
            selected = selectedFilter == TaskPriority.MEDIUM,
            onClick = { onFilterSelected(TaskPriority.MEDIUM) },
            label = { Text("重要度:中") },
        )
        FilterChip(
            selected = selectedFilter == TaskPriority.LOW,
            onClick = { onFilterSelected(TaskPriority.LOW) },
            label = { Text("重要度:低") },
        )
    }
}

/**
 * タスク一覧画面のUIコンテンツを表示するComposable関数
 * @param unfinishedTasks 未完了のタスクのリスト
 * @param finishedTasks 完了したタスクのリスト
 * @param onClick タスククリック時のコールバック
 * @param onDelete タスク削除時のコールバック
 * @param onComplete タスク完了時のコールバック
 */
@Composable
fun TaskListContent(
    unfinishedTasks: List<Task>,
    finishedTasks: List<Task>,
    onClick: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onComplete: (Task) -> Unit,
) {
    Column(

    ) {
        Text(text = "未完了のタスク")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(unfinishedTasks.size) {
                val task = unfinishedTasks[it]
                TaskCard(
                    task = task,
                    onClick = { onClick(task) },
                    onDelete = { onDelete(task) },
                    onComplete = { onComplete(task) }
                )
            }
        }
        Text(text = "完了したタスク")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(finishedTasks.size) {
                val task = finishedTasks[it]
                TaskCard(
                    task = task,
                    onClick = { onClick(task) },
                    onDelete = { onDelete(task) },
                    onComplete = { onComplete(task) },
                )
            }
        }
    }
}

/**
 * タスクカードのUIを表示するComposable関数
 * @param task タスクのデータ
 * @param onClick タスククリック時のコールバック
 * @param onDelete タスク削除時のコールバック
 * @param onComplete タスク完了時のコールバック
 */
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    color: Long = task.color // デフォルトはタスクの色
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
            Column {
                Text(text = task.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "重要度: ${TaskPriority.fromLevel(task.importance).label}",
                    fontSize = 16.sp
                )
                Text(text = "期日：${task.deadline}", fontSize = 16.sp)
                Text(text = "達成度：${task.progress}%", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.clickable { onDelete() }
            )
        }
    }
}