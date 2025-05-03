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
            HomeScreen(navController = navController)
        }
        composable(Route.AddTask) {
            AddTaskScreen(navController = navController)
        }
        composable(
            route = "${Route.EditTask}/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = androidx.navigation.NavType.IntType
                }
            )
        ) {
            EditTaskScreen(navController = navController)
        }
    }
}