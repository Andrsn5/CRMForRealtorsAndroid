package com.company.crm.data.mapper

import com.company.crm.data.model.dto.PhotoDto
import com.company.crm.data.model.entity.PhotoEntity
import com.company.crm.domain.model.Photo

fun PhotoEntity.toDomain(): Photo = Photo(
    id = this.id,
    photoUrl = this.photoUrl,
    caption = this.caption,
    displayOrder = this.displayOrder,
    objectId = this.objectId
)

fun PhotoDto.toEntity(): PhotoEntity = PhotoEntity(
    id = this.id,
    photoUrl = this.photoUrl,
    caption = this.caption,
    displayOrder = this.displayOrder,
    objectId = this.objectId,
    updatedAt = System.currentTimeMillis()
)

fun Photo.toDto(): PhotoDto = PhotoDto(
    id = this.id,
    photoUrl = this.photoUrl,
    caption = this.caption,
    displayOrder = this.displayOrder,
    objectId = this.objectId
)