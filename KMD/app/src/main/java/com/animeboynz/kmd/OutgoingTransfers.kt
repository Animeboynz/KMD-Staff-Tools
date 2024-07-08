import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.animeboynz.kmd.R
import com.animeboynz.kmd.Transfer
import com.animeboynz.kmd.TransferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutgoingTransfers(
    navController: NavController,
    transferViewModel: TransferViewModel = viewModel()
) {
    val transfers by transferViewModel.allTransfers.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outgoing Transfers") },
                actions = {
                    IconButton(
                        onClick = {
                            // Handle add transfer click
                            navController.navigate("add_transfer_route")
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
            if (transfers.isEmpty()) {
                Text(text = "No transfer info available")
            } else {
                TransferList(transfers = transfers)
            }
        }
    }
}

@Composable
fun TransferList(transfers: List<Transfer>) {
    LazyColumn {
        items(transfers) { transfer ->
            TransferListItem(transfer = transfer)
        }
    }
}

@Composable
fun TransferListItem(transfer: Transfer) {
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
