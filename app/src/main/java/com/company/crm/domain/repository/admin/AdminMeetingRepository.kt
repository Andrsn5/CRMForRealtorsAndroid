package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Meeting
import kotlinx.coroutines.flow.Flow

interface AdminMeetingRepository {
    fun observeAllMeetings(): Flow<List<Meeting>>
    suspend fun refreshAllMeetings()
    suspend fun getMeetingById(id: Int): Meeting?
    suspend fun createMeeting(meeting: Meeting)
    suspend fun updateMeeting(meeting: Meeting)
    suspend fun deleteMeeting(id: Int)
}