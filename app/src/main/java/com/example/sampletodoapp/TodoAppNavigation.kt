package com.example.sampletodoapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sampletodoapp.compose.addtask.AddTaskScreen
import com.example.sampletodoapp.compose.addtask.AddTaskViewModel
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
            HomeScreen()
        }
        composable(Route.AddTask) {
            val viewModel = AddTaskViewModel()
            AddTaskScreen(viewModel = viewModel, navController = navController)
        }
    }
}