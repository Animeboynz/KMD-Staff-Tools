package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class BackupBarcode(
    @ProtoNumber(1) val sku: String = "",
    @ProtoNumber(2) val color: String = "",
    @ProtoNumber(3) val size: String = "",
    @ProtoNumber(4) val name: String = "",
    @ProtoNumber(5) val pieceBarcode: String? = null,
    @ProtoNumber(6) val gtin: String? = null,
)

@Serializable
data class BackupColor(
    @ProtoNumber(1) val colorCode: String,
    @ProtoNumber(2) val colorName: String,
)

@Serializable
data class BackupData(
    @ProtoNumber(1) val barcodes: List<BackupBarcode> = emptyList(),
    @ProtoNumber(2) val colors: List<BackupColor> = emptyList(),
)

fun BackupBarcode.toEntity(): BarcodesEntity {
    return BarcodesEntity(
        sku = this.sku,
        color = this.color,
        size = this.size,
        name = this.name,
        pieceBarcode = this.pieceBarcode,
        gtin = this.gtin,
    )
}

fun BackupBarcode.toSKUEntity(): ProductsEntity {
    return ProductsEntity(
        sku = this.sku,
        name = this.name,
    )
}

fun BackupColor.toEntity(): ColorsEntity {
    return ColorsEntity(
        colorCode = this.colorCode,
        colorName = this.colorName
    )
}