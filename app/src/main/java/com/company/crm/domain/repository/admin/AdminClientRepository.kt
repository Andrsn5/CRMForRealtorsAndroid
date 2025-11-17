package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface AdminClientRepository {
    fun observeAllClients(): Flow<List<Client>>
    suspend fun refreshAllClients()
    suspend fun getClientById(id: Int): Client?
    suspend fun createClient(client: Client)
    suspend fun updateClient(client: Client)
    suspend fun deleteClient(id: Int)
}