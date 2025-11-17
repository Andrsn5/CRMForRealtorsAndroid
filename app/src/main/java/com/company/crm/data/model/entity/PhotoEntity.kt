package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("photos")
data class PhotoEntity(
    @PrimaryKey val id: Int,
    val photoUrl: String,
    val caption: String? = null,
    val displayOrder: Int,
    val objectId: Int,
    val updatedAt: Long = System.currentTimeMillis()
)