package com.example.sampletodoapp.compose.edittask

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sampletodoapp.compose.addtask.DatePickerModal
import com.example.sampletodoapp.compose.addtask.ImportanceRadioButtons
import com.example.sampletodoapp.compose.addtask.convertMillisToDate
import com.example.sampletodoapp.room.TaskColors
import kotlinx.coroutines.launch

/**
 * タスク編集画面のUIを表示するComposable関数
 * @param viewModel タスク編集画面のViewModel
 */
@Composable
fun EditTaskScreen(
    viewModel: EditTaskViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    when (val state = uiState.value) {
        is EditTaskUiState.Loading -> {
            Text(text = "Loading...")
        }

        is EditTaskUiState.Edit -> {
            var showDatePicker by remember { mutableStateOf(false) }
            EditTaskContent(
                task = state,
                viewModel = viewModel,
                onNavigateToHome = onNavigateToHome,
                showDatePicker = showDatePicker,
                onShowDatePickerChange = { showDatePicker = it }
            )
        }

        is EditTaskUiState.Success -> {
            Text(text = "Success")
        }
    }
}

/**
 * タスク編集画面のUIコンテンツを表示するComposable関数
 * @param task 編集するタスクの状態
 * @param viewModel タスク編集画面のViewModel
 */
@Composable
fun EditTaskContent(
    task: EditTaskUiState.Edit,
    viewModel: EditTaskViewModel,
    onNavigateToHome: () -> Unit,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { onNavigateToHome() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
            )
            Text(
                text = "戻る",
                fontSize = 20.sp
            )

        }
        // タスク名の入力
        OutlinedTextField(
            value = task.title,
            onValueChange = { viewModel.updateTitle(newTitle = it) },
            label = { Text("タスクを入力") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        // 期限の選択
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "期限日: ${task.deadline}", fontSize = 20.sp)
            OutlinedButton(
                onClick = { onShowDatePickerChange(true) },
            ) {
                Text(text = "期日の選択")
            }
        }

        // 重要度の選択
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "重要度")
            ImportanceRadioButtons(
                selected = task.importance,
                onSelected = { viewModel.updateImportance(newImportance = it.level) }
            )
        }

        // TODO: 色の選択機能を実装
        SelectColorSection(
            selectedColor = task.color,
            onColorSelected = { viewModel.updateColor(newColor = it) }
        )

        // タスクの進捗を示すスライダー
        ProgressSlider(
            progress = task.progress,
            onProgressChange = { viewModel.updateProgress(newProgress = it) }
        )

        // タスクの完了状態切り替え
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "タスクの状態：${if (task.isDone) "完了" else "未完了"}")
            Switch(
                checked = task.isDone,
                onCheckedChange = {
                    viewModel.toggleIsDone()
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        // 更新ボタン
        Button(
            onClick = {
                scope.launch {
                    viewModel.saveTask()
                }
                onNavigateToHome()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "更新")
        }

        // 日付選択モーダル
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { date ->
                    onShowDatePickerChange(false)
                    viewModel.updateDeadline(
                        newDeadline = date?.let { convertMillisToDate(it) } ?: ""
                    )
                },
                onDismiss = { onShowDatePickerChange(false) }
            )
        }
    }
}


@Composable
fun ProgressSlider(
    progress: Int,
    onProgressChange: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = progress.toFloat(),
            onValueChange = { newPosition ->
                onProgressChange(newPosition.toInt())
            },
            steps = 9,
            valueRange = 0f..100f
        )
        Text(text = "達成度：${progress}%")
    }
}

@Composable
fun SelectColorSection(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit = {}
) {
    val colors = TaskColors.colors


    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "色の選択", modifier = Modifier.padding(end = 8.dp))
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .border(
                        width = if (selectedColor == color) 3.dp else 1.dp,
                        color = if (selectedColor == color) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

