package com.example.sampletodoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sampletodoapp.compose.FakeAddTaskViewModel
import com.example.sampletodoapp.compose.addtask.AddTaskScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTaskUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addTaskTest() {
        val fakeViewModel = FakeAddTaskViewModel()

        composeTestRule.setContent {
            AddTaskScreen(viewModel = fakeViewModel, onNavigateToHome = {})
        }
        composeTestRule.onNodeWithTag("fab_add_task")

        // タスクのタイトルを入力
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("input_task_title")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("input_task_title")
            .assertIsDisplayed()
            .performClick()
            .performTextInput("SampleTask")
        // タスクの重要度を選択
        composeTestRule.onNodeWithTag("priority_1").performClick()
        // タスクを保存
        composeTestRule.onNodeWithTag("button_save_task").performClick()
    }
}