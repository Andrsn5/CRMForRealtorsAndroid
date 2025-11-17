package com.company.crm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.crm.data.model.entity.ObjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDao {
    @Query("SELECT * FROM objects WHERE id = :id")
    suspend fun getById(id: Int): ObjectEntity?

    @Query("SELECT * FROM objects")
    fun observeAll(): Flow<List<ObjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(objectEntity: ObjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objects: List<ObjectEntity>)

    @Update
    suspend fun update(objectEntity: ObjectEntity)

    @Query("DELETE FROM objects WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM objects")
    suspend fun clearAll()
}