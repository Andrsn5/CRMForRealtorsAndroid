package com.company.crm.data.repository.user

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.PhotoDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Photo
import com.company.crm.domain.repository.user.UserPhotoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class UserPhotoRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val photoDao: PhotoDao,
    override val prefs: UserPreferences
) : UserPhotoRepository, BaseRepository() {

    override fun observeMyPhotos(): Flow<List<Photo>> {
        return getCurrentEmployeeIdFlow().flatMapConcat { employeeId ->
            photoDao.observePhotosForEmployee(employeeId).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun refreshMyPhotos() {
        val employeeId = getCurrentEmployeeId()
        // Для пользователей получаем фото через доступные объекты
        val remote = api.getPhotosForEmployee(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            photoDao.deleteByEmployeeId(employeeId)
            photoDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch photos: ${remote.message}")
        }
    }

    override suspend fun getMyPhotoById(id: Int): Photo? {
        val employeeId = getCurrentEmployeeId()
        val photo = photoDao.getById(id)
        return if (photo != null && photoDao.isPhotoAccessibleByEmployee(id, employeeId)) {
            photo.toDomain()
        } else {
            null
        }
    }

    override suspend fun getPhotosByObjectId(objectId: Int): Flow<List<Photo>> {
        return photoDao.observeByObjectId(objectId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createPhoto(photo: Photo) {
        val employeeId = getCurrentEmployeeId()
        val response = api.createPhoto(employeeId, photo.toDto())

        if (response.success) {
            response.data?.let { photoDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create photo: ${response.message}")
        }
    }
}