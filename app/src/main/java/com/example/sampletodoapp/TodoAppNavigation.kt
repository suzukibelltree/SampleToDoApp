package com.example.sampletodoapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sampletodoapp.compose.addtask.AddTaskScreen
import com.example.sampletodoapp.compose.addtask.AddTaskViewModel
import com.example.sampletodoapp.compose.home.HomeScreen
import com.example.sampletodoapp.compose.home.HomeViewModel

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
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(Route.AddTask) {
            val viewModel: AddTaskViewModel = hiltViewModel()
            AddTaskScreen(viewModel = viewModel, navController = navController)
        }
    }
}