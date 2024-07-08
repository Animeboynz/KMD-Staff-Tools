package com.animeboynz.kmd

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: Transfer)

    @Query("SELECT * FROM transfers")
    suspend fun getAllTransfers(): List<Transfer>
}