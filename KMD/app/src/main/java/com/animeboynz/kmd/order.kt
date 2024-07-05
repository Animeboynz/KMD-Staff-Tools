import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// Order.kt - Data Model
data class Order(
    val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val customerMembership: String,
    val employeeName: String,
    val dateRequiredBy: String,
    val items: List<String>
)


class OrdersViewModel : ViewModel() {
    // Sample data to start with
    private val _createdOrders = mutableStateListOf<Order>(
        Order(
            "KMD-183-0001",
            "John Doe",
            "1234567890",
            "Gold Member",
            "Alice",
            "2024-07-10",
            listOf("SKU-001", "SKU-002")
        )
    )

    val createdOrders: List<Order>
        get() = _createdOrders

    fun addOrder(order: Order) {
        _createdOrders.add(order)
    }
}
