package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class DealDto(
    val id: Int,
    val dealNumber: String,
    val dealDate: String? = null,
    val dealAmount: String? = null,
    val commission: String? = null,
    val status: String,
    val clientId: Int? = null,
    val objectId: Int? = null
)