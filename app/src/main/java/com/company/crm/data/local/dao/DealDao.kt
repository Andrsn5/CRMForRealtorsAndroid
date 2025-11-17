package com.company.crm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.crm.data.model.entity.DealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {
    @Query("SELECT * FROM deals WHERE id = :id")
    suspend fun getById(id: Int): DealEntity?

    @Query("SELECT * FROM deals")
    fun observeAll(): Flow<List<DealEntity>>

    @Query("SELECT DISTINCT d.* FROM deals d " +
            "JOIN tasks t ON d.id = t.dealId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId")
    fun observeDealsForEmployee(employeeId: Int): Flow<List<DealEntity>>

    @Query("SELECT COUNT(*) FROM tasks t WHERE t.dealId = :dealId AND " +
            "(t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun isDealAccessibleByEmployee(dealId: Int, employeeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deal: DealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deals: List<DealEntity>)

    @Update
    suspend fun update(deal: DealEntity)

    @Query("DELETE FROM deals WHERE id IN " +
            "(SELECT d.id FROM deals d " +
            "JOIN tasks t ON d.id = t.dealId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun deleteByEmployeeId(employeeId: Int)

    @Query("DELETE FROM deals WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM deals")
    suspend fun clearAll()
}