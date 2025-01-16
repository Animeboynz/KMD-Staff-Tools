package com.animeboynz.kmd.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.Migrations
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.database.repository.CustomerOrderRepositoryImpl
import com.animeboynz.kmd.database.repository.EmployeeRepositoryImpl
import com.animeboynz.kmd.database.repository.OrderItemRepositoryImpl
import com.animeboynz.kmd.database.repository.ProductsRepositoryImpl
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import java.io.BufferedReader
import java.io.InputStreamReader

val DatabaseModule = module {
    single<ALMDatabase> {
        Room
            .databaseBuilder(androidContext(), ALMDatabase::class.java, "ALM.db")
            .addMigrations(migrations = Migrations)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    // Prepopulate database with products from CSV
                    CoroutineScope(Dispatchers.IO).launch {
                        val context = androidContext()
                        val inputStream = context.assets.open("products.csv")
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val products = mutableListOf<ProductsEntity>()

                        reader.useLines { lines ->
                            lines.drop(1).forEach { line -> // Skip the header row
                                val columns = line.split(",")
                                if (columns.size >= 2) {
                                    val sku = columns[0].trim()
                                    val name = columns[1].trim()
                                    products.add(ProductsEntity(sku = sku, name = name))
                                }
                            }
                        }

                        // Use the repository to insert the products
                        get<ProductsRepository>().insertAll(products)
                    }
                }
            })
            .build()
    }

    singleOf(::EmployeeRepositoryImpl).bind(EmployeeRepository::class)
    singleOf(::ProductsRepositoryImpl).bind(ProductsRepository::class)
    singleOf(::CustomerOrderRepositoryImpl).bind(CustomerOrderRepository::class)
    singleOf(::OrderItemRepositoryImpl).bind(OrderItemRepository::class)
}
