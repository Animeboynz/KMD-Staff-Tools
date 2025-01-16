package com.animeboynz.kmd.database.repository

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.EmployeeRepository

class EmployeeRepositoryImpl(
    private val database: ALMDatabase,
) : EmployeeRepository {
    override suspend fun upsert(employeeEntity: EmployeeEntity) {
        database.employeeDao().upsert(employeeEntity)
    }

    override suspend fun exists(employeeId: String): Boolean {
        return database.employeeDao().exists(employeeId)
    }

    override fun getEmployee(employeeId: String): EmployeeEntity? {
        return database.employeeDao().getEmployee(employeeId)
    }

    override fun getActiveEmployee(): Flow<List<EmployeeEntity?>> {
        return database.employeeDao().getActiveEmployee()
    }

    override fun getDisabledEmployee(): Flow<List<EmployeeEntity?>> {
        return database.employeeDao().getDisabledEmployee()
    }

    override suspend fun updateEmployeeId(name: String, id: String, status: String) {
        if (exists(name)) {
            database.employeeDao().updateEmployeeId(name, id)
        } else {
            database.employeeDao().upsert(
                EmployeeEntity(
                    employeeId = id,
                    employeeName = name,
                    employeeStatus = status,
                ),
            )
        }
    }

    override suspend fun updateEmployeeName(id: String, name: String, status: String) {
        if (exists(id)) {
            database.employeeDao().updateEmployeeName(id, name)
        } else {
            database.employeeDao().upsert(
                EmployeeEntity(
                    employeeId = id,
                    employeeName = name,
                    employeeStatus = status,
                ),
            )
        }
    }

    override suspend fun deleteEmployee(id: String) {
        return database.employeeDao().deleteEmployee(id)
    }

}
