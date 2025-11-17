package com.company.crm.domain.model

data class Deal(
    val id: Int,
    val dealNumber: String,
    val dealDate: String? = null,
    val dealAmount: String? = null,
    val commission: String? = null,
    val status: String,
    val clientId: Int? = null,
    val objectId: Int? = null,
)