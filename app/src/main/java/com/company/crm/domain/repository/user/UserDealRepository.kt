package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Deal
import kotlinx.coroutines.flow.Flow

interface UserDealRepository {
    fun observeMyDeals(): Flow<List<Deal>>
    suspend fun refreshMyDeals()
    suspend fun getMyDealById(id: Int): Deal?
    suspend fun createDeal(deal: Deal)
}