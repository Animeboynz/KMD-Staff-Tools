package com.animeboynz.kmd.domain

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
    }
}