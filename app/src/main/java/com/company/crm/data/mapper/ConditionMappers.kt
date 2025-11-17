package com.company.crm.data.mapper

import com.company.crm.data.model.dto.ConditionDto
import com.company.crm.data.model.entity.ConditionEntity
import com.company.crm.domain.model.Condition

fun ConditionEntity.toDomain(): Condition = Condition(
    id = this.id,
    conditionType = this.conditionType,
    description = this.description,
    deadline = this.deadline,
    priority = this.priority,
    status = this.status
)

fun ConditionDto.toEntity(): ConditionEntity = ConditionEntity(
    id = this.id,
    conditionType = this.conditionType,
    description = this.description,
    deadline = this.deadline,
    priority = this.priority,
    status = this.status,
    updatedAt = System.currentTimeMillis()
)

fun Condition.toDto(): ConditionDto = ConditionDto(
    id = this.id,
    conditionType = this.conditionType,
    description = this.description,
    deadline = this.deadline,
    priority = this.priority,
    status = this.status
)