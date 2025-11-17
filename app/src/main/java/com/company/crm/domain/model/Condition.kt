package com.company.crm.domain.model

data class Condition(
    val id: Int,
    val conditionType: String,
    val description: String,
    val deadline: String? = null,
    val priority: String,
    val status: String,
)