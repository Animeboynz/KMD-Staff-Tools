package com.animeboynz.kmd.domain

import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.EmployeeEntity

interface EmployeeRepository {
    @Upsert
    suspend fun upsert(employeeEntity: EmployeeEntity)

    suspend fun exists(path: String): Boolean

    fun getEmployee(employeeId: String): EmployeeEntity?

    fun getActiveEmployee(): Flow<List<EmployeeEntity?>>

    fun getDisabledEmployee(): Flow<List<EmployeeEntity?>>

    suspend fun updateEmployeeId(name: String, id: String, status: String)

    suspend fun updateEmployeeName(id: String, name: String, status: String)

    fun updateEmployeeStatus(id: String, status: String)

    suspend fun deleteEmployee(id: String)

    fun deleteAllEmployees()
}
