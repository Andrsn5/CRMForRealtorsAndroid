package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("clients")
data class ClientEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String? = null,
    val clientType: String,
    val budget: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)