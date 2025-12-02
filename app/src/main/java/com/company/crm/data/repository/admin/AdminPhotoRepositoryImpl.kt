package com.company.crm.data.repository.admin

import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.PhotoDao
import com.company.crm.data.mapper.toDomain
import com.company.crm.data.mapper.toDto
import com.company.crm.data.mapper.toEntity
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.BaseRepository
import com.company.crm.domain.model.Photo
import com.company.crm.domain.repository.admin.AdminPhotoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class AdminPhotoRepositoryImpl @Inject constructor(
    override val api: ApiService,
    private val photoDao: PhotoDao,
    override val prefs: UserPreferences
) : AdminPhotoRepository, BaseRepository() {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun observeAllPhotos(): Flow<List<Photo>> {
        return requireAdminRoleFlow().flatMapLatest {
            getCurrentEmployeeIdFlow().flatMapLatest { employeeId ->
                photoDao.observeAll().map { list ->
                    list.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun refreshAllPhotos() {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val remote = api.getAllPhotos(employeeId)
        if (remote.success) {
            val entities = remote.data?.map { it.toEntity() } ?: emptyList()
            photoDao.clearAll()
            photoDao.insertAll(entities)
        } else {
            throw Exception("Failed to fetch all photos: ${remote.message}")
        }
    }

    override suspend fun getPhotoById(id: Int): Photo? {
        requireAdminRole()
        return photoDao.getById(id)?.toDomain()
    }

    override suspend fun getPhotosByObjectId(objectId: Int): Flow<List<Photo>> {
        requireAdminRole()
        return photoDao.observeByObjectId(objectId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createPhoto(photo: Photo) {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.createPhoto(employeeId, photo.toDto())

        if (response.success) {
            response.data?.let { photoDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to create photo: ${response.message}")
        }
    }

    override suspend fun updatePhoto(photo: Photo) {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.updatePhoto(employeeId, photo.id, photo.toDto())

        if (response.success) {
            response.data?.let { photoDao.insert(it.toEntity()) }
        } else {
            throw Exception("Failed to update photo: ${response.message}")
        }
    }

    override suspend fun deletePhoto(id: Int) {
        requireAdminRole()
        val employeeId = getCurrentEmployeeId()
        val response = api.deletePhoto(employeeId, id)

        if (response.success) {
            photoDao.deleteById(id)
        } else {
            throw Exception("Failed to delete photo: ${response.message}")
        }
    }
}