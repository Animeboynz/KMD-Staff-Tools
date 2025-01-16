package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.EmployeeEntity

@Dao
interface EmployeeDao {
    @Upsert
    suspend fun upsert(employeeEntity: EmployeeEntity)

    @Query("SELECT EXISTS(SELECT * FROM EmployeeEntity WHERE employeeId = :employeeId)")
    suspend fun exists(employeeId: String): Boolean

    @Query("SELECT * FROM EmployeeEntity WHERE employeeId = :employeeId LIMIT 1")
    fun getEmployee(employeeId: String): EmployeeEntity?

    @Query("SELECT * FROM EmployeeEntity WHERE employeeStatus = 'Active'")
    fun getActiveEmployee(): Flow<List<EmployeeEntity?>>

    @Query("SELECT * FROM EmployeeEntity WHERE employeeStatus = 'Disabled'")
    fun getDisabledEmployee(): Flow<List<EmployeeEntity?>>

    @Query("UPDATE EmployeeEntity SET employeeId = :id WHERE employeeName = :name")
    suspend fun updateEmployeeId(name: String, id: String)

    @Query("UPDATE EmployeeEntity SET employeeName = :name WHERE employeeId = :id")
    suspend fun updateEmployeeName(id: String, name: String)

    @Query("DELETE FROM EmployeeEntity WHERE employeeId = :id")
    suspend fun deleteEmployee(id: String)
}
