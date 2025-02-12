package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.presentation.components.DropdownItem

enum class Status(
    override val displayName: String,
    override val id: Int,
    override val extraData: Int?,
    override val extraString: String?
) : DropdownItem {
    NOT_ORDERED("Not Ordered", 0, null, null),
    ORDERED("Ordered", 1, null, null),
    ARRIVED_NOT_NOTIFIED("Arrived - Not Notified", 2, null, null),
    WAITING_FOR_PICKUP("Waiting for Pickup", 3, null, null),
    CANCELLED("Cancelled", 4, null, null),
    COMPLETED("Completed", 5, null, null);

    companion object {
        val entries: List<Status> = values().toList()

        fun fromDisplayName(displayName: String): Status? {
            return values().find { it.displayName == displayName }
        }
    }
}

data class ColorDropdownItem(
    val item: BarcodesEntity,
    val colors: List<ColorsEntity>
) : DropdownItem {
    override val displayName: String
        get() = item.color
    override val id: Int
        get() = item.color.hashCode() // Or any unique integer ID
    override val extraData: Int? = null
    override val extraString: String? = colors.find { it.colorCode == item.color }?.colorName
}

data class WebColorDropdownItem(
    val colorCode: String,
    val colors: String
) : DropdownItem {
    override val displayName: String
        get() = colorCode
    override val id: Int
        get() = colorCode.hashCode() // Or any unique integer ID
    override val extraData: Int? = null
    override val extraString: String = colors
}

data class SizeDropdownItem(
    val item: BarcodesEntity
) : DropdownItem {
    override val displayName: String
        get() = item.size
    override val id: Int
        get() = item.size.hashCode() // Or any unique integer ID
    override val extraData: Int? = null
    override val extraString: String? = null
}

data class WebSizeDropdownItem(
    val item: String
) : DropdownItem {
    override val displayName: String
        get() = item
    override val id: Int
        get() = item.hashCode() // Or any unique integer ID
    override val extraData: Int? = null
    override val extraString: String? = null
}