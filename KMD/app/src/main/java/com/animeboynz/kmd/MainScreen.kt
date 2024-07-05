package com.animeboynz.kmd

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "KMD Staff Tools",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White // Ensures the text color is visible on the custom top bar color
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF77a22f) // Custom color for the top bar
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RowItem(
                text = "Cash Count",
                iconResId = R.drawable.ic_price_change,
                onClick = { navController.navigate("cash_row_route") }
            )
            RowItem(
                text = "Search",
                iconResId = R.drawable.ic_search, // Ensure you have this drawable resource
                onClick = { navController.navigate("search_route") }
            )
            RowItem(
                text = "Offsite Inventory",
                iconResId = R.drawable.ic_offsite, // Ensure you have this drawable resource
                onClick = { navController.navigate("search_route") }
            )
            RowItem(
                text = "Log Transfer",
                iconResId = R.drawable.ic_outgoing, // Ensure you have this drawable resource
                onClick = { navController.navigate("outgoing_transfers_route") }
            )
            RowItem(
                text = "Transfer Requests",
                iconResId = R.drawable.ic_incoming, // Ensure you have this drawable resource
                onClick = { navController.navigate("search_route") }
            )
            RowItem(
                text = "Customer Orders",
                iconResId = R.drawable.ic_person, // Ensure you have this drawable resource
                onClick = { navController.navigate("search_route") }
            )
            // Add more rows here for additional tools
        }
    }
}

@Composable
fun RowItem(text: String, iconResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = text,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
