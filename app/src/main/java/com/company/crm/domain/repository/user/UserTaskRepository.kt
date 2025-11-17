package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface UserTaskRepository {
    fun observeMyTasks(): Flow<List<Task>>
    suspend fun refreshMyTasks()
    suspend fun getMyTaskById(id: Int): Task?
    suspend fun createTask(task: Task)
    suspend fun updateMyTask(task: Task)
    suspend fun deleteMyTask(id: Int)
}