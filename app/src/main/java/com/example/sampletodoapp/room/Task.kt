package com.example.sampletodoapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * タスクのデータクラス
 * @param id タスクのID
 * @param title タスクのタイトル
 * @param deadline タスクの期限日
 * @param importance タスクの重要度
 * @param progress タスクの進捗度
 * @param isDone タスクの完了状態
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val deadline: String,
    val importance: Int,
    val progress: Int = 0,
    val isDone: Boolean = false
)