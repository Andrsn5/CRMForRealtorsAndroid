package com.company.crm.domain.repository.admin

import kotlinx.coroutines.flow.Flow

interface AdminObjectRepository {
    fun observeAllObjects(): Flow<List<com.company.crm.domain.model.Object>>
    suspend fun refreshAllObjects()
    suspend fun getObjectById(id: Int): com.company.crm.domain.model.Object?
    suspend fun createObject(objects: com.company.crm.domain.model.Object)
    suspend fun updateObject(objects: com.company.crm.domain.model.Object)
    suspend fun deleteObject(id: Int)
}