package com.company.crm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.crm.data.model.entity.MeetingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings WHERE id = :id")
    suspend fun getById(id: Int): MeetingEntity?

    @Query("SELECT * FROM meetings")
    fun observeAll(): Flow<List<MeetingEntity>>

    @Query("SELECT DISTINCT m.* FROM meetings m " +
            "JOIN tasks t ON m.id = t.meetingId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId")
    fun observeMeetingsForEmployee(employeeId: Int): Flow<List<MeetingEntity>>

    @Query("SELECT COUNT(*) FROM tasks t WHERE t.meetingId = :meetingId AND " +
            "(t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun isMeetingAccessibleByEmployee(meetingId: Int, employeeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meeting: MeetingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meetings: List<MeetingEntity>)

    @Update
    suspend fun update(meeting: MeetingEntity)

    @Query("DELETE FROM meetings WHERE id IN " +
            "(SELECT m.id FROM meetings m " +
            "JOIN tasks t ON m.id = t.meetingId " +
            "WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId)")
    suspend fun deleteByEmployeeId(employeeId: Int)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM meetings")
    suspend fun clearAll()
}