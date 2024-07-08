package com.animeboynz.kmd

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transfer::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transfersDao(): TransferDao
}