package com.company.crm.domain.model

data class Object(
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