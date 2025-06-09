package com.example.sampletodoapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sampletodoapp.compose.addtask.AddTaskScreen
import com.example.sampletodoapp.compose.edittask.EditTaskScreen
import com.example.sampletodoapp.compose.home.HomeScreen

/**
 * アプリケーションのナビゲーションを管理するComposable関数
 */
@Composable
fun TodoAppNavigation(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable(Route.Home) {
            HomeScreen(
                onNavigateToEditTask = { taskId ->
                    navController.navigate("${Route.EditTask}/$taskId")
                }
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