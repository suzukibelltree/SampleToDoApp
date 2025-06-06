package com.example.sampletodoapp.room

import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    fun getAllTasks(): Flow<List<Task>>
    fun getUnfinishedTasks(): Flow<List<Task>>
    fun getFinishedTasks(): Flow<List<Task>>
    fun loadTaskById(taskId: Int): Flow<Task>
}