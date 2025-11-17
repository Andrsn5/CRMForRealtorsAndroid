package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id:Int,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    val priority: String,
    val status: String,
    val responsibleId: Int,
    val creatorId: Int? = null,
    val clientId: Int? = null,
    val objectId: Int? = null,
    val meetingId: Int? = null,
    val dealId: Int? = null,
    val conditionId: Int? = null
)