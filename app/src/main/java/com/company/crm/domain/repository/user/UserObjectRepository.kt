package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Object
import kotlinx.coroutines.flow.Flow

interface UserObjectRepository {
    fun observeMyObjects(): Flow<List<com.company.crm.domain.model.Object>>
    suspend fun refreshMyObjects()
    suspend fun getMyObjectById(id: Int): com.company.crm.domain.model.Object?
    suspend fun createObject(objects: Object)
}