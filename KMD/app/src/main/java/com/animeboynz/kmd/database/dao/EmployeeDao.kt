package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.animeboynz.kmd.database.entities.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Upsert
    suspend fun upsert(employeeEntity: EmployeeEntity)

    @Query("SELECT EXISTS(SELECT * FROM EmployeeEntity WHERE employeeId = :employeeId)")
    suspend fun exists(employeeId: String): Boolean

    @Query("SELECT * FROM EmployeeEntity WHERE employeeId = :employeeId LIMIT 1")
    fun getEmployee(employeeId: String): EmployeeEntity?

    @Query("SELECT * FROM EmployeeEntity")
    fun getAllEmployees(): Flow<List<EmployeeEntity?>>

    @Query("SELECT * FROM EmployeeEntity WHERE employeeStatus = 'Active'")
    fun getActiveEmployees(): Flow<List<EmployeeEntity?>>

    @Query("SELECT * FROM EmployeeEntity WHERE employeeStatus = 'Disabled'")
    fun getDisabledEmployees(): Flow<List<EmployeeEntity?>>

    @Query("UPDATE EmployeeEntity SET employeeId = :id WHERE employeeName = :name")
    suspend fun updateEmployeeId(name: String, id: String)

    @Query("UPDATE EmployeeEntity SET employeeName = :name WHERE employeeId = :id")
    suspend fun updateEmployeeName(id: String, name: String)

    @Query("UPDATE EmployeeEntity SET employeeStatus = :status WHERE employeeId = :id")
    fun updateEmployeeStatus(id: String, status: String)

    @Query("DELETE FROM EmployeeEntity WHERE employeeId = :id")
    suspend fun deleteEmployee(id: String)

    @Query("DELETE FROM EmployeeEntity")
    fun deleteAllEmployees()
}
