package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class CustomerOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0, // Auto-generated ID for the order
    val orderDate: String,
    val employeeId: String, //1094536

    val customerName: String, //John Doe
    val customerPhone: String, //021 000 0000
    val customerMics: String, //john@doe.com

    val notes: String,
    val status: String //Completed, In Progress, Pending
)
