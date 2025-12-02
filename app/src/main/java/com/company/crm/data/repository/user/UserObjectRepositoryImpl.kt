package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.ObjectDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Object
import com.company.crm.domain.repository.user.UserObjectRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class UserObjectRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val objectDao: ObjectDao,
    override val prefs: UserPreferences
) : UserObjectRepository, BaseRepository() {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeMyObjects(): Flow<List<Object>> {
        return requireManagerRoleFlow().flatMapLatest {
            getCurrentEmployeeIdFlow().flatMapLatest { employeeId ->
                objectDao.observeAll().map { list ->
                    list.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun refreshMyObjects() {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val remote = api.getObjectsForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            // Для пользователей сохраняем все полученные объекты
            objectDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch objects: ${remote.message}")
        }
    }

    override suspend fun getMyObjectById(id: Int): Object? {
        getCurrentRole()
        // Для пользователей доступны все объекты (фильтрация на бэкенде)
        return objectDao.getById(id)?.toDomain()
    }

    override suspend fun createObject(objects: Object) {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.createObject(employeeId, objects.toDto())

        if (response.success) {
            response.data?.let { objectDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create object: ${response.message}")
        }
    }
}