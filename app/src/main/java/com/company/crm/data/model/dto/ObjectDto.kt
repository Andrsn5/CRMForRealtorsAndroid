package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ObjectDto(
    val id: Int,
    val title: String,
    val address: String? = null,
    val objectType: String,
    val dealType: String,
    val price: String? = null,
    val area: String? = null,
    val rooms: Int? = null,
    val bathrooms: Int? = null,
    val status: String
)