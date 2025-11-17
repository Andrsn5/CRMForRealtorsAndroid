package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.TaskDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Task
import com.company.crm.domain.repository.user.UserTaskRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class UserTaskRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val taskDao: TaskDao,
    override val prefs: UserPreferences
) : UserTaskRepository, BaseRepository() {

    override fun observeMyTasks(): Flow<List<Task>> {
        return getCurrentEmployeeIdFlow().flatMapConcat { employeeId ->
            taskDao.observeTasksForEmployee(employeeId).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun refreshMyTasks() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getTasksForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            // Удаляем только задачи пользователя перед обновлением
            taskDao.deleteByEmployeeId(employeeId)
            taskDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch tasks: ${remote.message}")
        }
    }

    override suspend fun getMyTaskById(id: Int): Task? {
        val employeeId = getCurrentEmployeeId()
        val task = taskDao.getById(id)
        return if (task != null && (task.responsibleId == employeeId || task.creatorId == employeeId)) {
            task.toDomain()
        } else {
            null
        }
    }

    override suspend fun createTask(task: Task) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createTask(employeeId, task.toDto())

        if (response.success) {
            response.data?.let { taskDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create task: ${response.message}")
        }
    }

    override suspend fun updateMyTask(task: Task) {
        val employeeId = getCurrentEmployeeId()
        // Проверяем, что пользователь имеет доступ к задаче
        val existingTask = taskDao.getById(task.id)
        if (existingTask == null || (existingTask.responsibleId != employeeId && existingTask.creatorId != employeeId)) {
            throw Exception("Access denied to this task")
        }

        val response = api.updateTask(employeeId, task.id, task.toDto())
        if (response.success) {
            response.data?.let { taskDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update task: ${response.message}")
        }
    }

    override suspend fun deleteMyTask(id: Int) {
        val employeeId = getCurrentEmployeeId()
        // Проверяем права на удаление (только создатель)
        val existingTask = taskDao.getById(id)
        if (existingTask == null || existingTask.creatorId != employeeId) {
            throw Exception("Only task creator can delete the task")
        }

        val response = api.deleteTask(employeeId, id)
        if (response.success) {
            taskDao.deleteById(id)
        } else {
            throw Exception("Failed to delete task: ${response.message}")
        }
    }
}