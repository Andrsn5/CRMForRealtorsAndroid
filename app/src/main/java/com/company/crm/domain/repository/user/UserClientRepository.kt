package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface UserClientRepository {
    fun observeMyClients(): Flow<List<Client>>
    suspend fun refreshMyClients()
    suspend fun getMyClientById(id: Int): Client?
    suspend fun createClient(client: Client)
}