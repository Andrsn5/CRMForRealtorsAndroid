package com.company.crm.data.mapper

import com.company.crm.data.model.dto.MeetingDto
import com.company.crm.data.model.entity.MeetingEntity
import com.company.crm.domain.model.Meeting

fun MeetingEntity.toDomain(): Meeting = Meeting(
    id = this.id,
    title = this.title,
    meetingDate = this.meetingDate,
    location = this.location,
    description = this.description,
    status = this.status,
    clientId = this.clientId,
    objectId = this.objectId
)

fun MeetingDto.toEntity(): MeetingEntity = MeetingEntity(
    id = this.id,
    title = this.title,
    meetingDate = this.meetingDate,
    location = this.location,
    description = this.description,
    status = this.status,
    clientId = this.clientId,
    objectId = this.objectId,
    updatedAt = System.currentTimeMillis()
)

fun Meeting.toDto(): MeetingDto = MeetingDto(
    id = this.id,
    title = this.title,
    meetingDate = this.meetingDate,
    location = this.location,
    description = this.description,
    status = this.status,
    clientId = this.clientId,
    objectId = this.objectId
)