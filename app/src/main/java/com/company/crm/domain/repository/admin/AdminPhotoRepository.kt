package com.company.crm.domain.repository.admin

import com.company.crm.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface AdminPhotoRepository {
    fun observeAllPhotos(): Flow<List<Photo>>
    suspend fun refreshAllPhotos()
    suspend fun getPhotoById(id: Int): Photo?
    suspend fun getPhotosByObjectId(objectId: Int): Flow<List<Photo>>
    suspend fun createPhoto(photo: Photo)
    suspend fun updatePhoto(photo: Photo)
    suspend fun deletePhoto(id: Int)
}