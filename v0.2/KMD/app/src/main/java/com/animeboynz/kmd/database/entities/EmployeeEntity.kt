package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.animeboynz.kmd.presentation.components.DropdownItem

@Entity
data class EmployeeEntity(
//    @PrimaryKey
//    val path: String,
//    val anilistId: Long? = null,
//    val aniDBId: Long? = null,

    @PrimaryKey
    val employeeId: String,
    val employeeName: String,
    val employeeStatus: String,
)
