package com.company.crm.domain.repository.user

import com.company.crm.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface UserPhotoRepository {
    fun observeMyPhotos(): Flow<List<Photo>>
    suspend fun refreshMyPhotos()
    suspend fun getMyPhotoById(id: Int): Photo?
    suspend fun getPhotosByObjectId(objectId: Int): Flow<List<Photo>>
    suspend fun createPhoto(photo: Photo)
}