package com.animeboynz.kmd.presentation.components

import com.animeboynz.kmd.database.entities.EmployeeEntity

data class EmployeeDropdownItem(
    val employee: EmployeeEntity
) : DropdownItem {
    override val displayName: String
        get() = employee.employeeName // Use employeeName as the display name
    override val id: Int
        get() = employee.employeeId.hashCode() // Or any unique integer ID
    override val extraData: Int?
        get() = employee.employeeId.toInt() // Or any additional data you want to show
    override val extraString: String? = null
}