package com.example.sampletodoapp.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TasksRepository {
    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasksFLow()
    }

    override fun loadTaskById(taskId: Int): Flow<Task> {
        return taskDao.loadTaskById(taskId)
    }
}