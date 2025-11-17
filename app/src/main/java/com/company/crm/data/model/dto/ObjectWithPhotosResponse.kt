package com.company.crm.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ObjectWithPhotosResponse(
    val objects: ObjectDto,
    val photos: List<PhotoDto>
)