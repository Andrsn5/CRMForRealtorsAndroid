package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: Int,
    val photoUrl: String,
    val caption: String? = null,
    val displayOrder: Int,
    val objectId: Int
)