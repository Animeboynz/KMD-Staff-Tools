package com.animeboynz.kmd

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers")
    suspend fun getAllTransfers(): List<Transfer>

    @Insert
    suspend fun insertTransfers(vararg transfers: Transfer)
}
