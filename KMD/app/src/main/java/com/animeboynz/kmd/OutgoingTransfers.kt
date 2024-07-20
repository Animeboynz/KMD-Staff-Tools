import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.animeboynz.kmd.R
import com.animeboynz.kmd.TransferItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutgoingTransfers(
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outgoing Transfers") },
                actions = {
                    IconButton(
                        onClick = {
                            // Handle add transfer click
                            //navController.navigate("add_transfer_route")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Add Transfer"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ///

            ///
        }
    }
}

@Composable
fun TransferList(transfers: List<TransferItem>) {
    LazyColumn {
        items(transfers) { transfer ->
            TransferListItem(transfer = transfer)
        }
    }
}

@Composable
fun TransferListItem(transfer: TransferItem) {
    ListItem(
        modifier = Modifier.padding(vertical = 8.dp),
        headlineContent = { Text("Transfer ID: ${transfer.transferId}") },
        supportingContent = {
            Column {
                Text("Tracking: ${transfer.tracking}")
                Text("From: ${transfer.fromLocation}")
                Text("To: ${transfer.toLocation}")
            }
        }
    )
}

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey val transferId: String,
    val tracking: String,
    val fromLocation: String,
    val toLocation: String,
    val notes: String
)
