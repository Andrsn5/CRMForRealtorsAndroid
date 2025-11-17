package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.ConditionDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Condition
import com.company.crm.domain.repository.user.UserConditionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class UserConditionRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val conditionDao: ConditionDao,
    override val prefs: UserPreferences
) : UserConditionRepository, BaseRepository() {

    override fun observeMyConditions(): Flow<List<Condition>> {
        return getCurrentEmployeeIdFlow().flatMapConcat { employeeId ->
            // Для пользователей показываем условия привязанные к их задачам
            conditionDao.observeConditionsForEmployee(employeeId).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun refreshMyConditions() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getConditionsForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            // Удаляем только условия пользователя перед обновлением
            conditionDao.deleteByEmployeeId(employeeId)
            conditionDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch conditions: ${remote.message}")
        }
    }

    override suspend fun getMyConditionById(id: Int): Condition? {
        val employeeId = getCurrentEmployeeId()
        val condition = conditionDao.getById(id)
        // Проверяем, что условие привязано к задаче пользователя
        return if (condition != null && conditionDao.isConditionAccessibleByEmployee(id, employeeId)) {
            condition.toDomain()
        } else {
            null
        }
    }

    override suspend fun createCondition(condition: Condition) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createCondition(employeeId, condition.toDto())

        if (response.success) {
            response.data?.let { conditionDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create condition: ${response.message}")
        }
    }
}