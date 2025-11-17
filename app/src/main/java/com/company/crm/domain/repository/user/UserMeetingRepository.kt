package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Meeting
import kotlinx.coroutines.flow.Flow

interface UserMeetingRepository {
    fun observeMyMeetings(): Flow<List<Meeting>>
    suspend fun refreshMyMeetings()
    suspend fun getMyMeetingById(id: Int): Meeting?
    suspend fun createMeeting(meeting: Meeting)
}