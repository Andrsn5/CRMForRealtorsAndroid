package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.EmployeeDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Employee
import com.company.crm.domain.repository.user.UserEmployeeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class UserEmployeeRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val employeeDao: EmployeeDao,
    override val prefs: UserPreferences
) : UserEmployeeRepository, BaseRepository() {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeMyProfile(): Flow<Employee?> {
        return requireManagerRoleFlow().flatMapConcat{
            getCurrentEmployeeIdFlow().flatMapConcat { employeeId ->
                employeeDao.observeEmployeeById(employeeId).map { entity ->
                    entity?.toDomain()
                }
            }
        }
    }

    override suspend fun refreshMyProfile() {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        val remote = api.getMyProfile(employeeId)
        if (remote.success) {
            remote.data?.let {
                employeeDao.insert(it.toEntity())
            }
        } else {
            throw Exception("Failed to fetch profile: ${remote.message}")
        }
    }

    override suspend fun getMyProfile(): Employee? {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        return employeeDao.getById(employeeId)?.toDomain()
    }

    override suspend fun updateMyProfile(employee: Employee) {
        getCurrentRole()
        val employeeId = getCurrentEmployeeId()
        // Убеждаемся, что сотрудник обновляет только свой профиль
        if (employee.id != employeeId) {
            throw Exception("You can only update your own profile")
        }

        val response = api.updateEmployee(employeeId, employeeId, employee.toDto())
        if (response.success) {
            response.data?.let { employeeDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update profile: ${response.message}")
        }
    }
}