package com.example.sampletodoapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sampletodoapp.ui.theme.SampleToDoAppTheme

/**
 * SampleTodoAppのメインComposable関数
 */
@Composable
fun SampleTodoApp() {
    SampleToDoAppTheme {
        TodoAppNavigation(
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}