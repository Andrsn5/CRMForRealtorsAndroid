package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Deal
import kotlinx.coroutines.flow.Flow

interface AdminDealRepository {
    fun observeAllDeals(): Flow<List<Deal>>
    suspend fun refreshAllDeals()
    suspend fun getDealById(id: Int): Deal?
    suspend fun createDeal(deal: Deal)
    suspend fun updateDeal(deal: Deal)
    suspend fun deleteDeal(id: Int)
}