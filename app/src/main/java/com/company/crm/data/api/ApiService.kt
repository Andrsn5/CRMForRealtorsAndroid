package com.company.crm.data.api

import com.company.crm.data.model.dto.ApiResponse
import com.company.crm.data.model.dto.ClientDto
import com.company.crm.data.model.dto.ConditionDto
import com.company.crm.data.model.dto.DashboardStats
import com.company.crm.data.model.dto.DealDto
import com.company.crm.data.model.dto.EmployeeDto
import com.company.crm.data.model.dto.LoginRequest
import com.company.crm.data.model.dto.MeetingDto
import com.company.crm.data.model.dto.ObjectDto
import com.company.crm.data.model.dto.ObjectWithPhotosResponse
import com.company.crm.data.model.dto.PhotoDto
import com.company.crm.data.model.dto.TaskDto


interface ApiService {
    suspend fun login(request: LoginRequest): ApiResponse<EmployeeDto>
    suspend fun getDashboardStats(employeeId: Int): ApiResponse<DashboardStats>


    suspend fun getTasksForEmployee(employeeId: Int): ApiResponse<List<TaskDto>>

    // Admin endpoints
    suspend fun getAllTasks(employeeId: Int): ApiResponse<List<TaskDto>>

    // Common endpoints (access control on backend)
    suspend fun getTask(employeeId: Int, id: Int): ApiResponse<TaskDto>
    suspend fun createTask(employeeId: Int, task: TaskDto): ApiResponse<TaskDto>
    suspend fun updateTask(employeeId: Int, id: Int, task: TaskDto): ApiResponse<TaskDto>
    suspend fun deleteTask(employeeId: Int, id: Int): ApiResponse<String>

    // User endpoints
    suspend fun getClientsForEmployee(employeeId: Int): ApiResponse<List<ClientDto>>

    // Admin endpoints
    suspend fun getAllClients(employeeId: Int): ApiResponse<List<ClientDto>>

    // Common endpoints
    suspend fun getClient(employeeId: Int, id: Int): ApiResponse<ClientDto>
    suspend fun createClient(employeeId: Int, client: ClientDto): ApiResponse<ClientDto>
    suspend fun updateClient(employeeId: Int, id: Int, client: ClientDto): ApiResponse<ClientDto>
    suspend fun deleteClient(employeeId: Int, id: Int): ApiResponse<String>

    // ===== OBJECTS =====
    // User endpoints
    suspend fun getObjectsForEmployee(employeeId: Int): ApiResponse<List<ObjectDto>>

    // Admin endpoints
    suspend fun getAllObjects(employeeId: Int): ApiResponse<List<ObjectDto>>

    // Common endpoints
    suspend fun getObject(employeeId: Int, id: Int): ApiResponse<ObjectWithPhotosResponse>
    suspend fun createObject(employeeId: Int, objectDto: ObjectDto): ApiResponse<ObjectDto>
    suspend fun updateObject(employeeId: Int, id: Int, objectDto: ObjectDto): ApiResponse<ObjectDto>
    suspend fun deleteObject(employeeId: Int, id: Int): ApiResponse<String>
    // ===== MEETINGS =====
    // User endpoints
    suspend fun getMeetingsForEmployee(employeeId: Int): ApiResponse<List<MeetingDto>>

    // Admin endpoints
    suspend fun getAllMeetings(employeeId: Int): ApiResponse<List<MeetingDto>>

    // Common endpoints
    suspend fun getMeeting(employeeId: Int, id: Int): ApiResponse<MeetingDto>
    suspend fun createMeeting(employeeId: Int, meetingDto: MeetingDto): ApiResponse<MeetingDto>
    suspend fun updateMeeting(employeeId: Int, id: Int, meetingDto: MeetingDto): ApiResponse<MeetingDto>
    suspend fun deleteMeeting(employeeId: Int, id: Int): ApiResponse<String>
    // ===== DEALS =====
    // User endpoints
    suspend fun getDealsForEmployee(employeeId: Int): ApiResponse<List<DealDto>>

    // Admin endpoints
    suspend fun getAllDeals(employeeId: Int): ApiResponse<List<DealDto>>

    // Common endpoints
    suspend fun getDeal(employeeId: Int, id: Int): ApiResponse<DealDto>
    suspend fun createDeal(employeeId: Int, dealDto: DealDto): ApiResponse<DealDto>
    suspend fun updateDeal(employeeId: Int, id: Int, dealDto: DealDto): ApiResponse<DealDto>
    suspend fun deleteDeal(employeeId: Int, id: Int): ApiResponse<String>
    // ===== CONDITIONS =====
    // User endpoints
    suspend fun getConditionsForEmployee(employeeId: Int): ApiResponse<List<ConditionDto>>

    // Admin endpoints
    suspend fun getAllConditions(employeeId: Int): ApiResponse<List<ConditionDto>>

    // Common endpoints
    suspend fun getCondition(employeeId: Int, id: Int): ApiResponse<ConditionDto>
    suspend fun createCondition(employeeId: Int, conditionDto: ConditionDto): ApiResponse<ConditionDto>
    suspend fun updateCondition(employeeId: Int, id: Int, conditionDto: ConditionDto): ApiResponse<ConditionDto>
    suspend fun deleteCondition(employeeId: Int, id: Int): ApiResponse<String>
    // ===== EMPLOYEES =====
    // User endpoints
    suspend fun getMyProfile(employeeId: Int): ApiResponse<EmployeeDto>

    // Admin endpoints
    suspend fun getAllEmployees(employeeId: Int): ApiResponse<List<EmployeeDto>>

    // Common endpoints
    suspend fun getEmployee(employeeId: Int, id: Int): ApiResponse<EmployeeDto>
    suspend fun createEmployee(employeeId: Int, employeeDto: EmployeeDto): ApiResponse<EmployeeDto>
    suspend fun updateEmployee(employeeId: Int, id: Int, employeeDto: EmployeeDto): ApiResponse<EmployeeDto>
    suspend fun deleteEmployee(employeeId: Int, id: Int): ApiResponse<String>
    // ===== PHOTOS =====
    // User endpoints
    suspend fun getPhotosForEmployee(employeeId: Int): ApiResponse<List<PhotoDto>>
    suspend fun getObjectPhotosForEmployee(employeeId: Int, objectId: Int): ApiResponse<List<PhotoDto>>

    // Admin endpoints
    suspend fun getAllPhotos(employeeId: Int): ApiResponse<List<PhotoDto>>
    suspend fun getObjectPhotos(employeeId: Int, objectId: Int): ApiResponse<List<PhotoDto>>

    // Common endpoints
    suspend fun getPhoto(employeeId: Int, id: Int): ApiResponse<PhotoDto>
    suspend fun createPhoto(employeeId: Int, photoDto: PhotoDto): ApiResponse<PhotoDto>
    suspend fun updatePhoto(employeeId: Int, id: Int, photoDto: PhotoDto): ApiResponse<PhotoDto>
    suspend fun deletePhoto(employeeId: Int, id: Int): ApiResponse<String>
}
