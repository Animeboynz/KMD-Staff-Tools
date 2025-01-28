package com.animeboynz.kmd.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.Migrations
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.database.repository.BarcodesRepositoryImpl
import com.animeboynz.kmd.database.repository.CustomerOrderRepositoryImpl
import com.animeboynz.kmd.database.repository.EmployeeRepositoryImpl
import com.animeboynz.kmd.database.repository.OrderItemRepositoryImpl
import com.animeboynz.kmd.database.repository.ProductsRepositoryImpl
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
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

                    // Prepopulate database with products from CSV
                    CoroutineScope(Dispatchers.IO).launch {
                        val context = androidContext()
                        val inputStreamProducts = context.assets.open("products.csv")
                        val readerProducts = BufferedReader(InputStreamReader(inputStreamProducts))
                        val products = mutableListOf<ProductsEntity>()

                        readerProducts.useLines { lines ->
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

                        // Load barcodes from barcodes.csv
                        val inputStreamBarcodes = context.assets.open("barcodes.csv")
                        val readerBarcodes = BufferedReader(InputStreamReader(inputStreamBarcodes))
                        val barcodes = mutableListOf<BarcodesEntity>()

                        readerBarcodes.useLines { lines ->
                            lines.drop(1).forEach { line -> // Skip the header row
                                val columns = line.split(",")
                                if (columns.size >= 6) {
                                    val sku = columns[0].trim()
                                    val color = columns[1].trim()
                                    val size = columns[2].trim()
                                    val name = columns[3].trim()
                                    val pieceBarcode = columns[4].trim()
                                    val gtin = columns[5].trim()
                                    barcodes.add(BarcodesEntity(sku = sku, color = color, size = size, name = name, pieceBarcode = pieceBarcode, gtin = gtin))
                                }
                            }
                        }

                        // Use the repository to insert the barcodes
                        get<BarcodesRepository>().insertAll(barcodes)
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
}
