package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.ClientDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Client
import com.company.crm.domain.repository.admin.AdminClientRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminClientRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val clientDao: ClientDao,
    override val prefs: UserPreferences
) : AdminClientRepository, BaseRepository() {

    override fun observeAllClients(): Flow<List<Client>> {
        return clientDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun refreshAllClients() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllClients(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            clientDao.clearAll()
            clientDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all clients: ${remote.message}")
        }
    }

    override suspend fun getClientById(id: Int): Client? {
        return clientDao.getById(id)?.toDomain()
    }

    override suspend fun createClient(client: Client) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createClient(employeeId, client.toDto())

        if (response.success) {
            response.data?.let { clientDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create client: ${response.message}")
        }
    }

    override suspend fun updateClient(client: Client) {
        val employeeId = getCurrentEmployeeId()
        val response = api.updateClient(employeeId, client.id, client.toDto())

        if (response.success) {
            response.data?.let { clientDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update client: ${response.message}")
        }
    }

    override suspend fun deleteClient(id: Int) {
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteClient(employeeId, id)

        if (response.success) {
            clientDao.deleteById(id)
        } else {
            throw Exception("Failed to delete client: ${response.message}")
        }
    }
}