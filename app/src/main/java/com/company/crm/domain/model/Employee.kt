package com.company.crm.domain.model


data class Employee(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val role: String
)
