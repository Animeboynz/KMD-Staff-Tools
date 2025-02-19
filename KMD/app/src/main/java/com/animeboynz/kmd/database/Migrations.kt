package com.animeboynz.kmd.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table for StockCountEntity
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `StockCountEntity` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `location` TEXT NOT NULL,
                `boxId` TEXT NOT NULL,
                `productBarcode` TEXT NOT NULL,
                `quantity` INTEGER NOT NULL
            )
        """)
    }
}

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ProductsEntity ADD COLUMN category TEXT NOT NULL DEFAULT ''")
    }
}


val Migrations: Array<Migration> = arrayOf(
    migration_1_2,
    //migration_2_3,
)
