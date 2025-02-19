package com.animeboynz.kmd.di

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.Migrations
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.database.repository.BarcodesRepositoryImpl
import com.animeboynz.kmd.database.repository.ColorsRepositoryImpl
import com.animeboynz.kmd.database.repository.CustomerOrderRepositoryImpl
import com.animeboynz.kmd.database.repository.EmployeeRepositoryImpl
import com.animeboynz.kmd.database.repository.OrderItemRepositoryImpl
import com.animeboynz.kmd.database.repository.ProductsRepositoryImpl
import com.animeboynz.kmd.database.repository.StockCountRepositoryImpl
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.domain.importProtobufData
import com.animeboynz.kmd.domain.toEntity
import com.animeboynz.kmd.domain.toSKUEntity
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DatabaseModule = module {
    single<ALMDatabase> {
        Room
            .databaseBuilder(androidContext(), ALMDatabase::class.java, "ALM.db")
            .addMigrations(migrations = Migrations)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    // Prepopulate database with products
                    CoroutineScope(Dispatchers.IO).launch {
                        val context = androidContext()
                        var importedData = importProtobufData(context, null, "products.proto")

                        if (importedData != null) {
                            get<ProductsRepository>().insertAll(importedData.barcodes.map { it.toSKUEntity() })
                            get<BarcodesRepository>().insertAll(importedData.barcodes.map { it.toEntity() })
                            get<ColorsRepository>().insertAll(importedData.colors.map { it.toEntity() })
                        }
                    }
                }
            })
            .build()
    }

    singleOf(::EmployeeRepositoryImpl).bind(EmployeeRepository::class)
    singleOf(::ProductsRepositoryImpl).bind(ProductsRepository::class)
    singleOf(::CustomerOrderRepositoryImpl).bind(CustomerOrderRepository::class)
    singleOf(::OrderItemRepositoryImpl).bind(OrderItemRepository::class)
    singleOf(::BarcodesRepositoryImpl).bind(BarcodesRepository::class)
    singleOf(::ColorsRepositoryImpl).bind(ColorsRepository::class)
    singleOf(::StockCountRepositoryImpl).bind(StockCountRepository::class)
}
