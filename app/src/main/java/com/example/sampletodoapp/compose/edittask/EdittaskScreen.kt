package com.example.sampletodoapp.compose.edittask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sampletodoapp.compose.addtask.DatePickerModal
import com.example.sampletodoapp.compose.addtask.ImportanceRadioButtons
import com.example.sampletodoapp.compose.addtask.convertMillisToDate
import kotlinx.coroutines.launch

/**
 * タスク編集画面のUIを表示するComposable関数
 * @param viewModel タスク編集画面のViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun EditTaskScreen(
    viewModel: EditTaskViewModel = hiltViewModel(),
    navController: NavController,
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
                navController = navController,
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
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun EditTaskContent(
    task: EditTaskUiState.Edit,
    viewModel: EditTaskViewModel,
    navController: NavController,
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
        // タスク名の入力
        OutlinedTextField(
            value = task.title,
            onValueChange = { viewModel.updateTitle(newTitle = it) },
            label = { Text("タスクを入力") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        // 期限の選択
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                navController.navigate("home")
            }
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
