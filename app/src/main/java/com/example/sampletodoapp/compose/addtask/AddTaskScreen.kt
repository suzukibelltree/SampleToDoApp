package com.example.sampletodoapp.compose.addtask

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel(),
    navController: NavController
) {
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
                    navController.navigate("home")
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

@Composable
fun AddTaskInputContent(
    state: AddTaskUiState.Input,
    onTitleChange: (String) -> Unit,
    onDeadlineChange: (String) -> Unit,
    onImportanceChange: (TaskPriority) -> Unit,
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
                .padding(16.dp),
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
            OutlinedButton(onClick = { showDatePicker = true }) {
                Text("期日の選択")
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

        // 追加ボタン
        Button(
            onClick = {
                val task = Task(
                    title = state.title,
                    deadline = state.deadline,
                    importance = state.importance.level
                )
                scope.launch { onSave(task) }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

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
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

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
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RadioButton(
                    selected = (
                            priority == selected
                            ),
                    onClick = null
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