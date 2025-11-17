package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeetingDto(
    val id: Int,
    val title: String,
    val meetingDate: String? = null,
    val location: String? = null,
    val description: String? = null,
    val status: String,
    val clientId: Int? = null,
    val objectId: Int? = null
)