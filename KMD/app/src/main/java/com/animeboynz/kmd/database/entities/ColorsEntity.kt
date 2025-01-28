package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorsEntity(
    @PrimaryKey
    val colorCode: String,
    val colorName: String,
)
