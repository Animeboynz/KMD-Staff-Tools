package com.animeboynz.kmd

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class TransferItem(
    @PrimaryKey val transferId: String,
    @ColumnInfo(name = "tracking") val tracking: String,
    @ColumnInfo(name = "from")val fromLocation: String,
    @ColumnInfo(name = "to")val toLocation: String,
    @ColumnInfo(name = "notes")val notes: String
)

