package com.animeboynz.kmd.data

import KMD.Employees.Employees
import kotlinx.coroutines.flow.Flow

interface EmployeeDataSource {

    suspend fun getEmployeeById(id: Long): Employees?

    suspend fun getAllEmployees(): List<Employees>

    suspend fun deleteEmployeeById(id: Long)

    suspend fun insertEmployee(firstName: String, lastName: String, id: Long)

}