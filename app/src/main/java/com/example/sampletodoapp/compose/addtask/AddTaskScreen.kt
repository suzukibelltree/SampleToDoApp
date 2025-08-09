package com.example.sampletodoapp.compose.addtask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sampletodoapp.compose.edittask.SelectColorSection
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * タスク追加画面のUIを表示するComposable関数
 * @param viewModel タスク追加画面のViewModel
 */
@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
) {
    // correctAsStateにより、ViewModel側で管理されているUI状態を監視し、状態の変更を検知できる
    val uiState = viewModel.uiState.collectAsState()
    when (val state = uiState.value) {
        is AddTaskUiState.Input -> {
            AddTaskInputContent(
                state = state,
                onTitleChange = { viewModel.updateTitle(it) },
                onDeadlineChange = { viewModel.updateDeadline(it) },
                onImportanceChange = { viewModel.updateImportance(it.level) },
                onSave = { task ->
                    viewModel.saveTask(task)
                    onNavigateToHome() // タスク保存後にホーム画面へ戻る
                },
                onColorChange = { color ->
                    viewModel.updateColor(color)
                }
            )
        }

        is AddTaskUiState.Saving -> {
            // 保存中のUIを表示
            CircularProgressIndicator()
        }

        is AddTaskUiState.Success -> {
            // タスク保存成功時のUIを表示
        }
    }
}

/**
 * タスク追加画面の入力内容を表示するComposable関数
 * @param state タスク追加画面のUI状態
 * @param onTitleChange タイトル変更時のコールバック
 * @param onDeadlineChange 期限日変更時のコールバック
 * @param onImportanceChange 重要度変更時のコールバック
 * @param onSave タスク保存時のコールバック
 * 親コンポーザブルでイベントを制御できるようにしている
 */
@Composable
fun AddTaskInputContent(
    state: AddTaskUiState.Input,
    onTitleChange: (String) -> Unit,
    onDeadlineChange: (String) -> Unit,
    onImportanceChange: (TaskPriority) -> Unit,
    onColorChange: (Long) -> Unit,
    onSave: (Task) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // タイトル入力
        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("タスクを入力") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("input_task_title"),
            singleLine = true
        )

        // 期限選択
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "期限日: ${state.deadline}", fontSize = 20.sp)
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.testTag("button_open_date_picker")
            ) {
                Text(
                    text = "期日の選択",
                )
            }
        }

        // 重要度選択
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "重要度")
            ImportanceRadioButtons(
                selected = state.importance,
                onSelected = onImportanceChange
            )
        }

        SelectColorSection(
            selectedColor = state.color,
            onColorSelected = { color ->
                scope.launch { onColorChange(color) }
            }
        )

        // 追加ボタン
        Button(
            onClick = {
                val task = Task(
                    title = state.title,
                    deadline = state.deadline,
                    importance = state.importance.level,
                    color = state.color,
                )
                scope.launch { onSave(task) }
            },
            modifier = Modifier.testTag("button_save_task")
        ) {
            Text("追加")
        }

        // 日付ピッカー表示
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    showDatePicker = false
                    onDeadlineChange(millis?.let { convertMillisToDate(it) } ?: "")
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

/**
 * 日付ピッカーのモーダルを表示するComposable関数
 * @param onDateSelected 日付選択時のコールバック
 * @param onDismiss モーダルが閉じられた時のコールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    Box(
        modifier = Modifier.testTag("show_datepicker")
    ) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            modifier = Modifier.testTag("show_datepicker")
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * 重要度選択のラジオボタンを表示するComposable関数
 * @param selected 選択された重要度
 * @param onSelected 重要度選択時のコールバック
 */
@Composable
fun ImportanceRadioButtons(
    selected: TaskPriority,
    onSelected: (TaskPriority) -> Unit
) {
    val options = TaskPriority.entries.toTypedArray()
    Row(modifier = Modifier.selectableGroup()) {
        options.forEach { priority ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = (
                                priority == selected
                                ),
                        onClick = { onSelected(priority) },
                        role = Role.RadioButton,
                    )
                    .padding(16.dp)
                    .testTag("priority_${priority.level}"),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RadioButton(
                    selected = (
                            priority == selected
                            ),
                    onClick = null,
                    modifier = Modifier.testTag("priority_${priority.level}"),
                )
                Text(
                    text = priority.label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return formatter.format(Date(millis))
}