package com.company.crm.data.mapper

import com.company.crm.data.model.dto.TaskDto
import com.company.crm.data.model.entity.TaskEntity
import com.company.crm.domain.model.Task


fun TaskEntity.toDomain(): Task = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    dueDate = this.dueDate,
    priority = this.priority,
    status = this.status,
    responsibleId = this.responsibleId,
    creatorId = this.creatorId,
    clientId = this.clientId,
    objectId = this.objectId,
    meetingId = this.meetingId,
    dealId = this.dealId,
    conditionId = this.conditionId
)

// Преобразование DTO в Entity
fun TaskDto.toEntity(): TaskEntity = TaskEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    dueDate = this.dueDate,
    priority = this.priority,
    status = this.status,
    responsibleId = this.responsibleId,
    creatorId = this.creatorId,
    clientId = this.clientId,
    objectId = this.objectId,
    meetingId = this.meetingId,
    dealId = this.dealId,
    conditionId = this.conditionId,
    updatedAt = System.currentTimeMillis()
)

// Преобразование Domain в DTO
fun Task.toDto(): TaskDto = TaskDto(
    id = this.id,
    title = this.title,
    description = this.description,
    dueDate = this.dueDate,
    priority = this.priority,
    status = this.status,
    responsibleId = this.responsibleId,
    creatorId = this.creatorId,
    clientId = this.clientId,
    objectId = this.objectId,
    meetingId = this.meetingId,
    dealId = this.dealId,
    conditionId = this.conditionId
)
