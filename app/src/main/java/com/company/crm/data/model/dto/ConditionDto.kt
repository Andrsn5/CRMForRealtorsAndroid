package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConditionDto(
    val id: Int,
    val conditionType: String,
    val description: String,
    val deadline: String? = null,
    val priority: String,
    val status: String
)