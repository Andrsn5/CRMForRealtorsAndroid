package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Condition
import kotlinx.coroutines.flow.Flow

interface UserConditionRepository {
    fun observeMyConditions(): Flow<List<Condition>>
    suspend fun refreshMyConditions()
    suspend fun getMyConditionById(id: Int): Condition?
    suspend fun createCondition(condition: Condition)
}