package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EmployeeEntity(
    @PrimaryKey
    val employeeId: String,
    val employeeName: String,
    val employeeStatus: String,
)
