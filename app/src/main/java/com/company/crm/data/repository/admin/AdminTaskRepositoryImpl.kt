package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.TaskDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Task
import com.company.crm.domain.repository.admin.AdminTaskRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminTaskRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val taskDao: TaskDao,
    override val prefs: UserPreferences
) : AdminTaskRepository, BaseRepository() {

    override fun observeAllTasks(): Flow<List<Task>> {
        return taskDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun refreshAllTasks() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllTasks(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            taskDao.clearAll()
            taskDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all tasks: ${remote.message}")
        }
    }

    override suspend fun getTaskById(id: Int): Task? {
        return taskDao.getById(id)?.toDomain()
    }

    override suspend fun upsertTask(task: Task) {
        val employeeId = getCurrentEmployeeId()
        val response = if (task.id == 0) {
            api.createTask(employeeId, task.toDto())
        } else {
            api.updateTask(employeeId, task.id, task.toDto())
        }

        if (response.success) {
            response.data?.let { taskDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to upsert task: ${response.message}")
        }
    }

    override suspend fun deleteTask(id: Int) {
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteTask(employeeId, id)
        if (response.success) {
            taskDao.deleteById(id)
        } else {
            throw Exception("Failed to delete task: ${response.message}")
        }
    }
}