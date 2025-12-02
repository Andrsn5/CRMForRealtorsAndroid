package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.ClientDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Client
import com.company.crm.domain.repository.user.UserClientRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class UserClientRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val clientDao: ClientDao,
    override val prefs: UserPreferences
) : UserClientRepository, BaseRepository() {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeMyClients(): Flow<List<Client>> {
        return requireManagerRoleFlow().flatMapLatest {
            getCurrentEmployeeIdFlow().flatMapLatest { employeeId ->
                clientDao.observeAll().map { list ->
                    list.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun refreshMyClients() {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val remote = api.getClientsForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            clientDao.deleteByEmployeeId(employeeId)
            clientDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch clients: ${remote.message}")
        }
    }

    override suspend fun getMyClientById(id: Int): Client? {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val client = clientDao.getById(id)
        return if (client != null && clientDao.isClientAccessibleByEmployee(client.id, employeeId)) {
            client.toDomain()
        } else {
            null
        }
    }

    override suspend fun createClient(client: Client) {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.createClient(employeeId, client.toDto())

        if (response.success) {
            response.data?.let { clientDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create client: ${response.message}")
        }
    }
}