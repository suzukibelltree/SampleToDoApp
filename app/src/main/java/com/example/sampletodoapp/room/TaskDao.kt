package com.example.sampletodoapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasksFLow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun loadTaskById(taskId: Int): Flow<Task>
}