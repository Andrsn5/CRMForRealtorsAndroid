package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface UserEmployeeRepository {
    fun observeMyProfile(): Flow<Employee?>
    suspend fun refreshMyProfile()
    suspend fun getMyProfile(): Employee?
    suspend fun updateMyProfile(employee: Employee)
}