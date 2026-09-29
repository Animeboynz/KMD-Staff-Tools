package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.animeboynz.kmd.database.entities.PendingTransactionEntity

@Dao
interface PendingTransactionDao {
    @Insert
    fun insert(transaction: PendingTransactionEntity): Long

    @Query("SELECT * FROM PendingTransactionEntity WHERE synced = 0 ORDER BY id")
    fun getUnsynced(): List<PendingTransactionEntity>

    @Query("UPDATE PendingTransactionEntity SET synced = 1 WHERE id IN (:ids)")
    fun markSynced(ids: List<Long>)
}
