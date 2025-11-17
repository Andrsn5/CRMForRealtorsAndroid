package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.ConditionDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Condition
import com.company.crm.domain.repository.admin.AdminConditionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminConditionRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val conditionDao: ConditionDao,
    override val prefs: UserPreferences
) : AdminConditionRepository, BaseRepository() {

    override fun observeAllConditions(): Flow<List<Condition>> {
        return conditionDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun refreshAllConditions() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllConditions(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            conditionDao.clearAll()
            conditionDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all conditions: ${remote.message}")
        }
    }

    override suspend fun getConditionById(id: Int): Condition? {
        return conditionDao.getById(id)?.toDomain()
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

    override suspend fun updateCondition(condition: Condition) {
        val employeeId = getCurrentEmployeeId()
        val response = api.updateCondition(employeeId, condition.id, condition.toDto())

        if (response.success) {
            response.data?.let { conditionDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update condition: ${response.message}")
        }
    }

    override suspend fun deleteCondition(id: Int) {
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteCondition(employeeId, id)

        if (response.success) {
            conditionDao.deleteById(id)
        } else {
            throw Exception("Failed to delete condition: ${response.message}")
        }
    }
}