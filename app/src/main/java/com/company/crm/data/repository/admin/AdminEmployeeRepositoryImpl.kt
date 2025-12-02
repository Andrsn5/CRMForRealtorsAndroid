package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.EmployeeDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Employee
import com.company.crm.domain.repository.admin.AdminEmployeeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class AdminEmployeeRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val employeeDao: EmployeeDao,
    override val prefs: UserPreferences
) : AdminEmployeeRepository, BaseRepository() {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeAllEmployees(): Flow<List<Employee>> {
        return requireAdminRoleFlow().flatMapLatest {
            getCurrentEmployeeIdFlow().flatMapLatest { employeeId ->
                employeeDao.observeAll().map { list ->
                    list.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun refreshAllEmployees() {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllEmployees(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            employeeDao.clearAll()
            employeeDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all employees: ${remote.message}")
        }
    }

    override suspend fun getEmployeeById(id: Int): Employee? {
        requireAdminRole()
        return employeeDao.getById(id)?.toDomain()
    }

    override suspend fun createEmployee(employee: Employee) {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.createEmployee(employeeId, employee.toDto())

        if (response.success) {
            response.data?.let { employeeDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create employee: ${response.message}")
        }
    }

    override suspend fun updateEmployee(employee: Employee) {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.updateEmployee(employeeId, employee.id, employee.toDto())

        if (response.success) {
            response.data?.let { employeeDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update employee: ${response.message}")
        }
    }

    override suspend fun deleteEmployee(id: Int) {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteEmployee(employeeId, id)

        if (response.success) {
            employeeDao.deleteById(id)
        } else {
            throw Exception("Failed to delete employee: ${response.message}")
        }
    }
}