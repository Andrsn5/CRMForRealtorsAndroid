package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface AdminTaskRepository {
    fun observeAllTasks(): Flow<List<Task>>
    suspend fun refreshAllTasks()
    suspend fun getTaskById(id: Int): Task?
    suspend fun upsertTask(task: Task)
    suspend fun deleteTask(id: Int)
}