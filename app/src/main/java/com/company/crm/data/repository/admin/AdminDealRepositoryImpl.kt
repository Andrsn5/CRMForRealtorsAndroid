package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.DealDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Deal
import com.company.crm.domain.repository.admin.AdminDealRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminDealRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val dealDao: DealDao,
    override val prefs: UserPreferences
) : AdminDealRepository, BaseRepository() {

    override fun observeAllDeals(): Flow<List<Deal>> {
        return dealDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun refreshAllDeals() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllDeals(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            dealDao.clearAll()
            dealDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all deals: ${remote.message}")
        }
    }

    override suspend fun getDealById(id: Int): Deal? {
        return dealDao.getById(id)?.toDomain()
    }

    override suspend fun createDeal(deal: Deal) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createDeal(employeeId, deal.toDto())

        if (response.success) {
            response.data?.let { dealDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create deal: ${response.message}")
        }
    }

    override suspend fun updateDeal(deal: Deal) {
        val employeeId = getCurrentEmployeeId()
        val response = api.updateDeal(employeeId, deal.id, deal.toDto())

        if (response.success) {
            response.data?.let { dealDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update deal: ${response.message}")
        }
    }

    override suspend fun deleteDeal(id: Int) {
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteDeal(employeeId, id)

        if (response.success) {
            dealDao.deleteById(id)
        } else {
            throw Exception("Failed to delete deal: ${response.message}")
        }
    }
}