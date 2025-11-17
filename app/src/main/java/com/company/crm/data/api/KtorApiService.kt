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
import com.company.crm.data.prefs.UserPreferences
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class ApiServiceKtor @Inject constructor(
    private val client: HttpClient,
    private val prefs: UserPreferences
): ApiService {

    private suspend fun HttpRequestBuilder.authHeader() {
        val token = prefs.getToken()
        if (!token.isNullOrEmpty()) {
            header("Authorization", "Bearer $token")
        }
    }

    private suspend fun HttpRequestBuilder.employeeHeader(employeeId: Int) {
        header("X-Employee-Id", employeeId.toString())
    }

    private suspend fun HttpRequestBuilder.applyCommonHeaders(employeeId: Int) {
        employeeHeader(employeeId)
        authHeader()
    }

    // ===== AUTHENTICATION =====
    override suspend fun login(request: LoginRequest): ApiResponse<EmployeeDto> {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ===== DASHBOARD =====
    override suspend fun getDashboardStats(employeeId: Int): ApiResponse<DashboardStats> {
        return client.get("dashboard") {
            employeeHeader(employeeId)
            authHeader()
        }.body()
    }

    // ===== TASKS =====
    override suspend fun getTasksForEmployee(employeeId: Int): ApiResponse<List<TaskDto>> {
        return client.get("tasks/employee/$employeeId") {
            employeeHeader(employeeId)
            authHeader()
        }.body()
    }

    // Admin endpoints

    override suspend fun getAllTasks(employeeId: Int): ApiResponse<List<TaskDto>> {
        return client.get("tasks") {
            employeeHeader(employeeId)
            authHeader()
        }.body()
    }

    // Common endpoints

    override suspend fun getTask(employeeId: Int, id: Int): ApiResponse<TaskDto> {
        return client.get("tasks/$id") {
            employeeHeader(employeeId)
            authHeader()
        }.body()
    }

    override suspend fun createTask(employeeId: Int, task: TaskDto): ApiResponse<TaskDto> {
        return client.post("tasks") {
            contentType(ContentType.Application.Json)
            employeeHeader(employeeId)
            authHeader()
            setBody(task)
        }.body()
    }

    override suspend fun updateTask(employeeId: Int, id: Int, task: TaskDto): ApiResponse<TaskDto> {
        return client.put("tasks/$id") {
            contentType(ContentType.Application.Json)
            employeeHeader(employeeId)
            authHeader()
            setBody(task)
        }.body()
    }

    override suspend fun deleteTask(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("tasks/$id") {
            employeeHeader(employeeId)
            authHeader()
        }.body()
    }

    // ===== CLIENTS =====
    // User endpoints
    override suspend fun getClientsForEmployee(employeeId: Int): ApiResponse<List<ClientDto>> {
        return client.get("clients/employee/$employeeId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllClients(employeeId: Int): ApiResponse<List<ClientDto>> {
        return client.get("clients") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getClient(employeeId: Int, id: Int): ApiResponse<ClientDto> {
        return client.get("clients/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createClient(employeeId: Int, clientDto: ClientDto): ApiResponse<ClientDto> {
        return client.post("clients") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(clientDto)
        }.body()
    }

    override suspend fun updateClient(employeeId: Int, id: Int, clientDto: ClientDto): ApiResponse<ClientDto> {
        return client.put("clients/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(clientDto)
        }.body()
    }

    override suspend fun deleteClient(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("clients/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }




    // ===== OBJECTS =====
    // User endpoints
    override suspend fun getObjectsForEmployee(employeeId: Int): ApiResponse<List<ObjectDto>> {
        return client.get("objects/employee/$employeeId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllObjects(employeeId: Int): ApiResponse<List<ObjectDto>> {
        return client.get("objects") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getObject(employeeId: Int, id: Int): ApiResponse<ObjectWithPhotosResponse> {
        return client.get("objects/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createObject(employeeId: Int, objectDto: ObjectDto): ApiResponse<ObjectDto> {
        return client.post("objects") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(objectDto)
        }.body()
    }

    override suspend fun updateObject(employeeId: Int, id: Int, objectDto: ObjectDto): ApiResponse<ObjectDto> {
        return client.put("objects/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(objectDto)
        }.body()
    }

    override suspend fun deleteObject(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("objects/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // ===== MEETINGS =====
    // User endpoints
    override suspend fun getMeetingsForEmployee(employeeId: Int): ApiResponse<List<MeetingDto>> {
        return client.get("meetings/employee/$employeeId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllMeetings(employeeId: Int): ApiResponse<List<MeetingDto>> {
        return client.get("meetings") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getMeeting(employeeId: Int, id: Int): ApiResponse<MeetingDto> {
        return client.get("meetings/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createMeeting(employeeId: Int, meetingDto: MeetingDto): ApiResponse<MeetingDto> {
        return client.post("meetings") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(meetingDto)
        }.body()
    }

    override suspend fun updateMeeting(employeeId: Int, id: Int, meetingDto: MeetingDto): ApiResponse<MeetingDto> {
        return client.put("meetings/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(meetingDto)
        }.body()
    }

    override suspend fun deleteMeeting(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("meetings/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // ===== DEALS =====
    // User endpoints
    override suspend fun getDealsForEmployee(employeeId: Int): ApiResponse<List<DealDto>> {
        return client.get("deals/employee/$employeeId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllDeals(employeeId: Int): ApiResponse<List<DealDto>> {
        return client.get("deals") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getDeal(employeeId: Int, id: Int): ApiResponse<DealDto> {
        return client.get("deals/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createDeal(employeeId: Int, dealDto: DealDto): ApiResponse<DealDto> {
        return client.post("deals") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(dealDto)
        }.body()
    }

    override suspend fun updateDeal(employeeId: Int, id: Int, dealDto: DealDto): ApiResponse<DealDto> {
        return client.put("deals/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(dealDto)
        }.body()
    }

    override suspend fun deleteDeal(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("deals/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // ===== CONDITIONS =====
    // User endpoints
    override suspend fun getConditionsForEmployee(employeeId: Int): ApiResponse<List<ConditionDto>> {
        return client.get("conditions/employee/$employeeId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllConditions(employeeId: Int): ApiResponse<List<ConditionDto>> {
        return client.get("conditions") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getCondition(employeeId: Int, id: Int): ApiResponse<ConditionDto> {
        return client.get("conditions/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createCondition(employeeId: Int, conditionDto: ConditionDto): ApiResponse<ConditionDto> {
        return client.post("conditions") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(conditionDto)
        }.body()
    }

    override suspend fun updateCondition(employeeId: Int, id: Int, conditionDto: ConditionDto): ApiResponse<ConditionDto> {
        return client.put("conditions/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(conditionDto)
        }.body()
    }

    override suspend fun deleteCondition(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("conditions/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // ===== EMPLOYEES =====
    // User endpoints
    override suspend fun getMyProfile(employeeId: Int): ApiResponse<EmployeeDto> {
        return client.get("employees/profile") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllEmployees(employeeId: Int): ApiResponse<List<EmployeeDto>> {
        return client.get("employees") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getEmployee(employeeId: Int, id: Int): ApiResponse<EmployeeDto> {
        return client.get("employees/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createEmployee(employeeId: Int, employeeDto: EmployeeDto): ApiResponse<EmployeeDto> {
        return client.post("employees") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(employeeDto)
        }.body()
    }

    override suspend fun updateEmployee(employeeId: Int, id: Int, employeeDto: EmployeeDto): ApiResponse<EmployeeDto> {
        return client.put("employees/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(employeeDto)
        }.body()
    }

    override suspend fun deleteEmployee(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("employees/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }
    // ===== PHOTOS =====
    // User endpoints
    override suspend fun getPhotosForEmployee(employeeId: Int): ApiResponse<List<PhotoDto>> {
        return client.get("photos/employee/$employeeId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun getObjectPhotosForEmployee(employeeId: Int, objectId: Int): ApiResponse<List<PhotoDto>> {
        return client.get("photos/employee/$employeeId/object/$objectId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Admin endpoints
    override suspend fun getAllPhotos(employeeId: Int): ApiResponse<List<PhotoDto>> {
        return client.get("photos") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun getObjectPhotos(employeeId: Int, objectId: Int): ApiResponse<List<PhotoDto>> {
        return client.get("photos/object/$objectId") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    // Common endpoints
    override suspend fun getPhoto(employeeId: Int, id: Int): ApiResponse<PhotoDto> {
        return client.get("photos/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }

    override suspend fun createPhoto(employeeId: Int, photoDto: PhotoDto): ApiResponse<PhotoDto> {
        return client.post("photos") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(photoDto)
        }.body()
    }

    override suspend fun updatePhoto(employeeId: Int, id: Int, photoDto: PhotoDto): ApiResponse<PhotoDto> {
        return client.put("photos/$id") {
            contentType(ContentType.Application.Json)
            applyCommonHeaders(employeeId)
            setBody(photoDto)
        }.body()
    }

    override suspend fun deletePhoto(employeeId: Int, id: Int): ApiResponse<String> {
        return client.delete("photos/$id") {
            applyCommonHeaders(employeeId)
        }.body()
    }
}