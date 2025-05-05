package com.example.sampletodoapp

import android.content.Context
import com.example.sampletodoapp.room.TaskDao
import com.example.sampletodoapp.room.TaskDatabase
import com.example.sampletodoapp.room.TaskRepositoryImpl
import com.example.sampletodoapp.room.TasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase {
        return TaskDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTasksRepository(taskDao: TaskDao): TasksRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }
}

