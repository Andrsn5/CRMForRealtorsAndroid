package com.company.crm.data.mapper

import com.company.crm.data.model.dto.DealDto
import com.company.crm.data.model.entity.DealEntity
import com.company.crm.domain.model.Deal

fun DealEntity.toDomain(): Deal = Deal(
    id = this.id,
    dealNumber = this.dealNumber,
    dealDate = this.dealDate,
    dealAmount = this.dealAmount,
    commission = this.commission,
    status = this.status,
    clientId = this.clientId,
    objectId = this.objectId
)

fun DealDto.toEntity(): DealEntity = DealEntity(
    id = this.id,
    dealNumber = this.dealNumber,
    dealDate = this.dealDate,
    dealAmount = this.dealAmount,
    commission = this.commission,
    status = this.status,
    clientId = this.clientId,
    objectId = this.objectId,
    updatedAt = System.currentTimeMillis()
)

fun Deal.toDto(): DealDto = DealDto(
    id = this.id,
    dealNumber = this.dealNumber,
    dealDate = this.dealDate,
    dealAmount = this.dealAmount,
    commission = this.commission,
    status = this.status,
    clientId = this.clientId,
    objectId = this.objectId
)