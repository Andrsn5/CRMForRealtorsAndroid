package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.DealDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Deal
import com.company.crm.domain.repository.user.UserDealRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class UserDealRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val dealDao: DealDao,
    override val prefs: UserPreferences
) : UserDealRepository, BaseRepository() {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeMyDeals(): Flow<List<Deal>> {
        return requireManagerRoleFlow().flatMapLatest {
            getCurrentEmployeeIdFlow().flatMapLatest { employeeId ->
                dealDao.observeAll().map { list ->
                    list.map { it.toDomain() }
                }
            }
        }
    }


    override suspend fun refreshMyDeals() {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val remote = api.getDealsForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            dealDao.deleteByEmployeeId(employeeId)
            dealDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch deals: ${remote.message}")
        }
    }

    override suspend fun getMyDealById(id: Int): Deal? {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val deal = dealDao.getById(id)
        return if (deal != null && dealDao.isDealAccessibleByEmployee(id, employeeId)) {
            deal.toDomain()
        } else {
            null
        }
    }

    override suspend fun createDeal(deal: Deal) {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.createDeal(employeeId, deal.toDto())

        if (response.success) {
            response.data?.let { dealDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create deal: ${response.message}")
        }
    }
}