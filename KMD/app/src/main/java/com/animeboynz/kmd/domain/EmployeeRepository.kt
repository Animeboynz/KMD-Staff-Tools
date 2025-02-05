package com.animeboynz.kmd.domain

import androidx.room.Upsert
import com.animeboynz.kmd.database.entities.EmployeeEntity
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    @Upsert
    suspend fun upsert(employeeEntity: EmployeeEntity)

    suspend fun exists(path: String): Boolean

    fun getEmployee(employeeId: String): EmployeeEntity?

    fun getAllEmployees(): Flow<List<EmployeeEntity?>>

    fun getActiveEmployees(): Flow<List<EmployeeEntity?>>

    fun getDisabledEmployees(): Flow<List<EmployeeEntity?>>

    suspend fun updateEmployeeId(name: String, id: String, status: String)

    suspend fun updateEmployeeName(id: String, name: String, status: String)

    fun updateEmployeeStatus(id: String, status: String)

    suspend fun deleteEmployee(id: String)

    fun deleteAllEmployees()
}
