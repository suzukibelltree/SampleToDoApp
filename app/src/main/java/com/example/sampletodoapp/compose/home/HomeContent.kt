package com.example.sampletodoapp.compose.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.sampletodoapp.Route

@Composable
fun HomeTabContent(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("タスク一覧", "本日のタスク")
    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> {
                // タスク一覧のタブが選択された場合の処理
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToEditTask = { taskId ->
                        navController.navigate("${Route.EditTask}/$taskId")
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            1 -> {
                // 本日のタスクのタブが選択された場合の処理
                Text(
                    text = "本日のタスクはまだ実装されていません",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}