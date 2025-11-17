package com.company.crm.domain.model

data class Client(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String? = null,
    val clientType: String,
    val budget: String? = null,
)