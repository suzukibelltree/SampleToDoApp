package com.example.sampletodoapp

import app.cash.turbine.test
import com.example.sampletodoapp.compose.addtask.AddTaskUiState
import com.example.sampletodoapp.compose.addtask.AddTaskViewModel
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import com.example.sampletodoapp.room.TasksRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddTaskViewModelTest {
    val repository = mockk<TasksRepository>()
    private lateinit var viewModel: AddTaskViewModel

    // タイトル入力によるUI状態更新確認
    @Test
    fun `When the title is updated, the UI state reflects the change`() {
        viewModel = AddTaskViewModel(repository)

        // タイトルを更新
        val newTitle = "New Task Title"
        viewModel.updateTitle(newTitle)

        // UI状態が更新されたことを確認
        assert(viewModel.uiState.value is AddTaskUiState.Input)
        assert((viewModel.uiState.value as AddTaskUiState.Input).title == newTitle)
    }

    // 重要度入力によるUI状態更新確認
    @Test
    fun `When the deadline is updated, the UI state reflects the change`() {
        viewModel = AddTaskViewModel(repository)

        // 期限日を更新
        val newDeadline = "2025/01/01"
        viewModel.updateDeadline(newDeadline)

        // UI状態が更新されたことを確認
        assert(viewModel.uiState.value is AddTaskUiState.Input)
        assert((viewModel.uiState.value as AddTaskUiState.Input).deadline == newDeadline)
    }

    // タスク保存によるUI状態更新確認
    @Test
    fun `When the task is saved, the UI state reflects the change`() = runTest {
        viewModel = AddTaskViewModel(repository)
        // タスク保存処理のモック化
        coEvery { repository.insertTask(any()) } returns Unit
        // UI状態の監視をするブロック
        viewModel.uiState.test {
            viewModel.saveTask(
                Task(
                    id = 0,
                    title = "Test Task",
                    deadline = "2025/01/01",
                    importance = TaskPriority.MEDIUM.level,
                    color = 0xFFd3d3d3,
                    progress = 0,
                    isDone = false
                )
            )
            // 初期状態の確認(UI状態がInputであるか)
            assert(awaitItem() is AddTaskUiState.Input)

            // 保存中状態
            assert(awaitItem() is AddTaskUiState.Saving)

            // 保存成功状態
            assert(awaitItem() is AddTaskUiState.Success)
            coVerify { repository.insertTask(any()) }
        }
    }
}