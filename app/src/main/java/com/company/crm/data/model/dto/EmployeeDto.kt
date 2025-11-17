package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val role: String
)