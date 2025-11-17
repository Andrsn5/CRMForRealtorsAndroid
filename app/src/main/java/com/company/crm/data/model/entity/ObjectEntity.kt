package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("objects")
data class ObjectEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val address: String? = null,
    val objectType: String,
    val dealType: String,
    val price: String? = null,
    val area: String? = null,
    val rooms: Int? = null,
    val bathrooms: Int? = null,
    val status: String,
    val updatedAt: Long = System.currentTimeMillis()
)