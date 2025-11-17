package com.company.crm.domain.model

data class Photo(
    val id: Int,
    val photoUrl: String,
    val caption: String? = null,
    val displayOrder: Int,
    val objectId: Int
)