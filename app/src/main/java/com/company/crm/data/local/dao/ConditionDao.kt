package com.company.crm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.crm.data.model.entity.ConditionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConditionDao {
    @Query("SELECT * FROM conditions WHERE id = :id")
    suspend fun getById(id: Int): ConditionEntity?

    @Query("SELECT * FROM conditions")
    fun observeAll(): Flow<List<ConditionEntity>>

    @Query("SELECT DISTINCT c.* FROM conditions c " +
            "JOIN tasks t ON c.id = t.conditionId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId")
    fun observeConditionsForEmployee(employeeId: Int): Flow<List<ConditionEntity>>

    @Query("SELECT COUNT(*) FROM tasks t WHERE t.conditionId = :conditionId AND " +
            "(t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun isConditionAccessibleByEmployee(conditionId: Int, employeeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(condition: ConditionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conditions: List<ConditionEntity>)

    @Update
    suspend fun update(condition: ConditionEntity)

    @Query("DELETE FROM conditions WHERE id IN " +
            "(SELECT c.id FROM conditions c " +
            "JOIN tasks t ON c.id = t.conditionId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun deleteByEmployeeId(employeeId: Int)

    @Query("DELETE FROM conditions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM conditions")
    suspend fun clearAll()
}