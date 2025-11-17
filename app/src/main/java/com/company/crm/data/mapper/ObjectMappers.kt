package com.company.crm.data.mapper

import com.company.crm.data.model.dto.ObjectDto
import com.company.crm.data.model.entity.ObjectEntity
import com.company.crm.domain.model.Object

fun ObjectEntity.toDomain(): Object = Object(
    id = this.id,
    title = this.title,
    address = this.address,
    objectType = this.objectType,
    dealType = this.dealType,
    price = this.price,
    area = this.area,
    rooms = this.rooms,
    bathrooms = this.bathrooms,
    status = this.status
)

fun ObjectDto.toEntity(): ObjectEntity = ObjectEntity(
    id = this.id,
    title = this.title,
    address = this.address,
    objectType = this.objectType,
    dealType = this.dealType,
    price = this.price,
    area = this.area,
    rooms = this.rooms,
    bathrooms = this.bathrooms,
    status = this.status,
    updatedAt = System.currentTimeMillis()
)

fun Object.toDto(): ObjectDto = ObjectDto(
    id = this.id,
    title = this.title,
    address = this.address,
    objectType = this.objectType,
    dealType = this.dealType,
    price = this.price,
    area = this.area,
    rooms = this.rooms,
    bathrooms = this.bathrooms,
    status = this.status
)