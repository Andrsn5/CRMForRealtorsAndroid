package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.ObjectDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Object
import com.company.crm.domain.repository.admin.AdminObjectRepository
import com.company.crm.domain.repository.user.UserObjectRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class AdminObjectRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val objectDao: ObjectDao,
    override val prefs: UserPreferences
) : AdminObjectRepository, BaseRepository() {

    override fun observeAllObjects(): Flow<List<Object>> {
        return objectDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun refreshAllObjects() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllObjects(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            objectDao.clearAll()
            objectDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all objects: ${remote.message}")
        }
    }

    override suspend fun getObjectById(id: Int): Object? {
        return objectDao.getById(id)?.toDomain()
    }

    override suspend fun createObject(objects: Object) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createObject(employeeId, objects.toDto())

        if (response.success) {
            response.data?.let { objectDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create object: ${response.message}")
        }
    }

    override suspend fun updateObject(objects: Object) {
        val employeeId = getCurrentEmployeeId()
        val response = api.updateObject(employeeId, objects.id, objects.toDto())

        if (response.success) {
            response.data?.let { objectDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update object: ${response.message}")
        }
    }

    override suspend fun deleteObject(id: Int) {
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteObject(employeeId, id)

        if (response.success) {
            objectDao.deleteById(id)
        } else {
            throw Exception("Failed to delete object: ${response.message}")
        }
    }
}