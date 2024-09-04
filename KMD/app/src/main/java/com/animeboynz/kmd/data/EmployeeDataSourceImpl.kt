package com.animeboynz.kmd.data

import KMD.Employees.Employees
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList

import com.animeboynz.kmd.KMDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EmployeeDataSourceImpl(
    db: KMDB
): EmployeeDataSource {
    private val queries = db.employeesQueries

    override suspend fun getEmployeeById(id: Long): Employees? {
        return withContext(Dispatchers.IO){
            queries.getEmpById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getAllEmployees(): Flow<List<Employees>> {
        return withContext(Dispatchers.IO){
            queries.getAllEmp().asFlow().mapToList(coroutineContext)
        }
    }

    override suspend fun deleteEmployeeById(id: Long) {
        withContext(Dispatchers.IO){
            queries.deleteEmpById(id)
        }
    }

    override suspend fun insertEmployee(firstName: String, lastName: String, id: Long) {
        withContext(Dispatchers.IO){
            queries.insertEmp(id, firstName, lastName)
        }
    }
}