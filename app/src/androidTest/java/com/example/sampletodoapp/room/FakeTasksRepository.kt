package com.example.sampletodoapp.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeTasksRepository : TasksRepository {
    // メモリ上のタスクリスト
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    override suspend fun insertTask(task: Task) {
        val updatedList = tasksFlow.value.toMutableList()
        updatedList.add(task)
        tasksFlow.value = updatedList
    }

    override suspend fun deleteTask(task: Task) {
        val updatedList = tasksFlow.value.toMutableList()
        updatedList.remove(task)
        tasksFlow.value = updatedList
    }

    override suspend fun updateTask(task: Task) {
        val updatedList = tasksFlow.value.toMutableList()
        val index = updatedList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            updatedList[index] = task
            tasksFlow.value = updatedList
        }
    }

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow.asStateFlow()

    override fun getUnfinishedTasks(): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { !it.isDone } }

    override fun getFinishedTasks(): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.isDone } }

    override fun loadTaskById(taskId: Int): Flow<Task> =
        tasksFlow.map { list -> list.first { it.id == taskId } }
}
