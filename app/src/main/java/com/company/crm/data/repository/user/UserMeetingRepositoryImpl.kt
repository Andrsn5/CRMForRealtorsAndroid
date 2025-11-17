package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.MeetingDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Meeting
import com.company.crm.domain.repository.user.UserMeetingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class UserMeetingRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val meetingDao: MeetingDao,
    override val prefs: UserPreferences
) : UserMeetingRepository, BaseRepository() {

    override fun observeMyMeetings(): Flow<List<Meeting>> {
        return getCurrentEmployeeIdFlow().flatMapConcat { employeeId ->
            meetingDao.observeMeetingsForEmployee(employeeId).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun refreshMyMeetings() {
        val employeeId = getCurrentEmployeeId()
        val remote = api.getMeetingsForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            meetingDao.deleteByEmployeeId(employeeId)
            meetingDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch meetings: ${remote.message}")
        }
    }

    override suspend fun getMyMeetingById(id: Int): Meeting? {
        val employeeId = getCurrentEmployeeId()
        val meeting = meetingDao.getById(id)
        return if (meeting != null && meetingDao.isMeetingAccessibleByEmployee(id, employeeId)) {
            meeting.toDomain()
        } else {
            null
        }
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
}