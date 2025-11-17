package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("deals")
data class DealEntity(
    @PrimaryKey val id: Int,
    val dealNumber: String,
    val dealDate: String? = null,
    val dealAmount: String? = null,
    val commission: String? = null,
    val status: String,
    val clientId: Int? = null,
    val objectId: Int? = null,
    val updatedAt: Long = System.currentTimeMillis()
)