package com.animeboynz.kmd.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ProductsEntity ADD COLUMN category TEXT NOT NULL DEFAULT ''")
    }
}

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ProductsEntity ADD COLUMN category TEXT NOT NULL DEFAULT ''")
    }
}


val Migrations: Array<Migration> = arrayOf(
    //migration_1_2,
    //migration_2_3,
)
