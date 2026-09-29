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
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `LocationEntity` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `parentId` INTEGER,
                `name` TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_LocationEntity_parentId` ON `LocationEntity` (`parentId`)",
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `BoxEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `barcode` TEXT NOT NULL, `locationId` INTEGER NOT NULL, `lastCountedAt` INTEGER, FOREIGN KEY(`locationId`) REFERENCES `LocationEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_BoxEntity_barcode` ON `BoxEntity` (`barcode`)",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_BoxEntity_locationId` ON `BoxEntity` (`locationId`)",
        )

        // Collapse duplicate contents rows before the unique (box, barcode) index.
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `StockCountEntity_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `location` TEXT NOT NULL,
                `boxId` TEXT NOT NULL,
                `productBarcode` TEXT NOT NULL,
                `quantity` INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO `StockCountEntity_new` (`location`, `boxId`, `productBarcode`, `quantity`)
            SELECT MIN(`location`), `boxId`, `productBarcode`, SUM(`quantity`)
            FROM `StockCountEntity`
            GROUP BY `boxId`, `productBarcode`
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE `StockCountEntity`")
        db.execSQL("ALTER TABLE `StockCountEntity_new` RENAME TO `StockCountEntity`")
        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS `index_StockCountEntity_boxId_productBarcode`
            ON `StockCountEntity` (`boxId`, `productBarcode`)
            """.trimIndent(),
        )
    }
}

val migration_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `PendingTransactionEntity` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `boxBarcode` TEXT NOT NULL,
                `productBarcode` TEXT NOT NULL,
                `delta` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `deviceId` TEXT NOT NULL,
                `synced` INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_PendingTransactionEntity_synced` ON `PendingTransactionEntity` (`synced`)",
        )
    }
}

val Migrations: Array<Migration> = arrayOf(
    migration_1_2,
    migration_2_3,
    migration_3_4,
)
