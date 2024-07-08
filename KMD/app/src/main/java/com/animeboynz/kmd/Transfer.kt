package com.animeboynz.kmd

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey val transferId: String,
    val tracking: String,
    val fromLocation: String,
    val toLocation: String,
    val notes: String
)

