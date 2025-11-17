package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Condition
import kotlinx.coroutines.flow.Flow

interface AdminConditionRepository {
    fun observeAllConditions(): Flow<List<Condition>>
    suspend fun refreshAllConditions()
    suspend fun getConditionById(id: Int): Condition?
    suspend fun createCondition(condition: Condition)
    suspend fun updateCondition(condition: Condition)
    suspend fun deleteCondition(id: Int)
}