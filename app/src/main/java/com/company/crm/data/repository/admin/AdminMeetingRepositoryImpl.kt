package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.MeetingDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Meeting
import com.company.crm.domain.repository.admin.AdminMeetingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminMeetingRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val meetingDao: MeetingDao,
    override val prefs: UserPreferences
) : AdminMeetingRepository, BaseRepository() {

    override fun observeAllMeetings(): Flow<List<Meeting>> {
        return meetingDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun refreshAllMeetings() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllMeetings(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            meetingDao.clearAll()
            meetingDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all meetings: ${remote.message}")
        }
    }

    override suspend fun getMeetingById(id: Int): Meeting? {
        return meetingDao.getById(id)?.toDomain()
    }

    override suspend fun createMeeting(meeting: Meeting) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createMeeting(employeeId, meeting.toDto())

        if (response.success) {
            response.data?.let { meetingDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create meeting: ${response.message}")
        }
    }

    override suspend fun updateMeeting(meeting: Meeting) {
        val employeeId = getCurrentEmployeeId()
        val response = api.updateMeeting(employeeId, meeting.id, meeting.toDto())

        if (response.success) {
            response.data?.let { meetingDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update meeting: ${response.message}")
        }
    }

    override suspend fun deleteMeeting(id: Int) {
        val employeeId = getCurrentEmployeeId()
        val response = api.deleteMeeting(employeeId, id)

        if (response.success) {
            meetingDao.deleteById(id)
        } else {
            throw Exception("Failed to delete meeting: ${response.message}")
        }
    }
}