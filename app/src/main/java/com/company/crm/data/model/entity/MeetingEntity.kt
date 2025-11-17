package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("meetings")
data class MeetingEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val meetingDate: String? = null,
    val location: String? = null,
    val description: String? = null,
    val status: String,
    val clientId: Int? = null,
    val objectId: Int? = null,
    val updatedAt: Long = System.currentTimeMillis()
)