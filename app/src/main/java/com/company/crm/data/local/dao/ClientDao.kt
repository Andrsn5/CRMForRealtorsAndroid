package com.company.crm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.crm.data.model.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getById(id: Int): ClientEntity?

    @Query("SELECT * FROM clients")
    fun observeAll(): Flow<List<ClientEntity>>

    @Query("""
        SELECT DISTINCT c.* FROM clients c
        JOIN tasks t ON c.id = t.clientId 
        WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId
        ORDER BY c.firstName, c.lastName
    """)
    fun observeClientsForEmployee(employeeId: Int): Flow<List<ClientEntity>>

    @Query("""
        SELECT COUNT(*) FROM tasks t 
        WHERE t.clientId = :clientId AND (t.responsibleId = :employeeId OR t.creatorId = :employeeId)
    """)
    suspend fun isClientAccessibleByEmployee(clientId: Int, employeeId: Int): Boolean

    @Query("""
            DELETE FROM clients WHERE id IN (
            SELECT DISTINCT c.id FROM clients c
            JOIN tasks t ON c.id = t.clientId
            WHERE t.responsibleId = :employeeId OR t.creatorId = :employeeId
    )""")

    suspend fun deleteByEmployeeId(employeeId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clients: List<ClientEntity>)

    @Update
    suspend fun update(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM clients")
    suspend fun clearAll()
}