package com.example.sampletodoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sampletodoapp.compose.addtask.AddTaskScreen
import com.example.sampletodoapp.compose.edittask.EditTaskScreen
import com.example.sampletodoapp.compose.home.HomeTabContent

/**
 * アプリケーションのナビゲーションを管理するComposable関数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAppNavigation(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when {
        currentRoute?.startsWith(Route.Home) == true -> "TODOアプリ"
        currentRoute?.startsWith(Route.AddTask) == true -> "タスク追加"
        currentRoute?.startsWith(Route.EditTask) == true -> "タスク編集"
        else -> ""
    }

    val showFab = currentRoute == Route.Home // TabA/Bのみ
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(Route.AddTask) },
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Route.Home) {
                HomeTabContent(
                    navController = navController,
                )
            }
            composable(Route.AddTask) {
                AddTaskScreen(
                    onNavigateToHome = {
                        navController.navigate(Route.Home)
                    }
                )
            }
            composable(
                route = "${Route.EditTask}/{taskId}",
                arguments = listOf(
                    navArgument("taskId") {
                        type = androidx.navigation.NavType.IntType
                    }
                )
            ) {
                EditTaskScreen(
                    onNavigateToHome = {
                        navController.navigate(Route.Home)
                    }
                )
            }
        }
    }

}