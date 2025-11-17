package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStats(
    val activeTasksCount: Int,
    val completedTasksCount: Int,
    val totalEmployees: Int
)