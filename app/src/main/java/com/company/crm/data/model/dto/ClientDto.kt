package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    val id: Int ,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String? = null,
    val clientType: String,
    val budget: String? = null
)