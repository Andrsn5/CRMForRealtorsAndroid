package com.company.crm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.crm.data.model.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getById(id: Int): PhotoEntity?

    @Query("SELECT * FROM photos")
    fun observeAll(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE objectId = :objectId ORDER BY displayOrder ASC")
    fun observeByObjectId(objectId: Int): Flow<List<PhotoEntity>>

    @Query("SELECT DISTINCT p.* FROM photos p " +
            "JOIN objects o ON p.objectId = o.id " +
            "JOIN tasks t ON o.id = t.objectId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId")
    fun observePhotosForEmployee(employeeId: Int): Flow<List<PhotoEntity>>

    @Query("SELECT COUNT(*) FROM photos p " +
            "JOIN objects o ON p.objectId = o.id " +
            "JOIN tasks t ON o.id = t.objectId " +
            "WHERE p.id = :photoId AND (t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun isPhotoAccessibleByEmployee(photoId: Int, employeeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Update
    suspend fun update(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id IN " +
            "(SELECT p.id FROM photos p " +
            "JOIN objects o ON p.objectId = o.id " +
            "JOIN tasks t ON o.id = t.objectId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun deleteByEmployeeId(employeeId: Int)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM photos WHERE objectId = :objectId")
    suspend fun deleteByObjectId(objectId: Int)

    @Query("DELETE FROM photos")
    suspend fun clearAll()
}