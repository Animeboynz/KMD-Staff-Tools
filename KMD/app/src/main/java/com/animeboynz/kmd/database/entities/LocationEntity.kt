package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["parentId"])])
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long?,
    val name: String,
)
