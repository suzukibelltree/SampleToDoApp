package com.example.sampletodoapp

import app.cash.turbine.test
import com.example.sampletodoapp.compose.home.HomeUiState
import com.example.sampletodoapp.compose.home.HomeViewModel
import com.example.sampletodoapp.room.Task
import com.example.sampletodoapp.room.TaskPriority
import com.example.sampletodoapp.room.TasksRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class HomeViewModelTest {
    val repository = mockk<TasksRepository>()
    private lateinit var viewModelTest: HomeViewModel

    @Test
    fun `When the repository returns a task, Success flows after Loading and the task is classified correctly`() {
        runTest {
            val FinishedTasks = listOf<Task>(
                Task(
                    id = 1,
                    title = "Test Task 1",
                    deadline = "2025/01/01",
                    importance = TaskPriority.HIGH.level,
                    color = 0xFF0000,
                    progress = 0,
                    isDone = true
                ),
                Task(
                    id = 2,
                    title = "Test Task 2",
                    deadline = "2025/01/02",
                    importance = TaskPriority.MEDIUM.level,
                    color = 0x00FF00,
                    progress = 0,
                    isDone = true
                )
            )
            val unfinishedTasks = listOf<Task>(
                Task(
                    id = 3,
                    title = "Test Task 3",
                    deadline = "2025/01/03",
                    importance = TaskPriority.LOW.level,
                    color = 0x0000FF,
                    progress = 0,
                    isDone = false
                )
            )
            // タスク取得処理のモック化
            every {
                repository.getFinishedTasks()
            } returns flowOf(FinishedTasks)
            every {
                repository.getUnfinishedTasks()
            } returns flowOf(unfinishedTasks)

            viewModelTest = HomeViewModel(repository)

            viewModelTest.uiState.test {
                // 初期状態の確認(UI状態がLoadingであるか)
                assert(awaitItem() is HomeUiState.Loading)

                // データ取得後の状態を確認
                val state = awaitItem()
                // UI状態がSuccessであることを確認
                assert(state is HomeUiState.Success)
                val successState = state as HomeUiState.Success

                // タスクが正しく取得されているか確認
                assert(successState.finishedTasks.size == FinishedTasks.size)
                assert(successState.unfinishedTasks.size == unfinishedTasks.size)
                assert(!successState.isEmpty)
            }
        }
    }
}