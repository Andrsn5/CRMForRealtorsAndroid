package com.company.crm.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("employees")
data class EmployeeEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val role: String,
    val updatedAt: Long = System.currentTimeMillis()
)