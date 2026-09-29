package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["synced"])])
data class PendingTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val boxBarcode: String,
    val productBarcode: String,
    val delta: Int,
    val createdAt: Long,
    val deviceId: String,
    val synced: Boolean = false,
)
