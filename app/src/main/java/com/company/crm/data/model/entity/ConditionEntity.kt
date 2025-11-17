package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("conditions")
data class ConditionEntity(
    @PrimaryKey val id: Int,
    val conditionType: String,
    val description: String,
    val deadline: String? = null,
    val priority: String,
    val status: String,
    val updatedAt: Long = System.currentTimeMillis()
)