package com.company.crm.domain.model

class Meeting(
    val id: Int,
    val title: String,
    val meetingDate: String? = null,
    val location: String? = null,
    val description: String? = null,
    val status: String,
    val clientId: Int? = null,
    val objectId: Int? = null,
)