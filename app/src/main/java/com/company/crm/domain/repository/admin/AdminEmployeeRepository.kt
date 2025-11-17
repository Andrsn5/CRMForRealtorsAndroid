package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface AdminEmployeeRepository {
    fun observeAllEmployees(): Flow<List<Employee>>
    suspend fun refreshAllEmployees()
    suspend fun getEmployeeById(id: Int): Employee?
    suspend fun createEmployee(employee: Employee)
    suspend fun updateEmployee(employee: Employee)
    suspend fun deleteEmployee(id: Int)
}