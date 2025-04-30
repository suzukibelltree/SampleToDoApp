package com.example.sampletodoapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sampletodoapp.ui.theme.SampleToDoAppTheme

@Composable
fun SampleTodoApp() {
    SampleToDoAppTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            floatingActionButton = {
                if (currentRoute == Route.Home) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Route.AddTask) },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                        )
                    }
                }
            }
        ) { innerPadding ->
            TodoAppNavigation(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                navController = navController
            )

        }
    }
}