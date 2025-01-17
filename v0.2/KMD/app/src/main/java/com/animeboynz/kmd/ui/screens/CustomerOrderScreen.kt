package com.animeboynz.kmd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.presentation.Screen
import com.github.k1rakishou.fsaf.FileManager
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.presentation.components.SelectItem
import com.animeboynz.kmd.ui.theme.spacing

class CustomerOrderScreen(val order: CustomerOrderEntity) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val fileManager = koinInject<FileManager>()
        val customerOrderRepository = koinInject<CustomerOrderRepository>()
        val screenModel = rememberScreenModel { CustomerOrderScreenModel(customerOrderRepository) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "KMD-183-${order.orderId}")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                screenModel.deleteOrder(order.orderId);
                                navigator.pop()
                                Toast.makeText(context, R.string.orders_delete_success, Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Delete, null)
                        }
                        IconButton(
                            onClick = {
                            }
                        ) {
                            Icon(Icons.Default.EditNote, null)
                        }
                    },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                CustomerOrderCard(order)

                SelectItem(
                    title = order.status,
                    present = true,
                    onClick = { },
                )



                Spacer(modifier = Modifier.weight(1f))

//                Text(text = order.orderDate)
//                Text(text = order.customerName)
//                Text(text = order.status)
//                Text(text = order.employeeId)
//                Text(text = order.customerMics)
//                Text(text = order.customerPhone)
//                Text(text = order.notes)

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
            }
        }
    }

    @Composable
    fun CustomerOrderCard(
        order: CustomerOrderEntity,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(R.string.orders_details),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                )

                // Order Date
                InfoRow(icon = Icons.Default.DateRange, text = order.orderDate)

                // Customer Name
                InfoRow(icon = Icons.Default.Person, text = order.customerName)

                // Customer Mics
                InfoRow(icon = Icons.Default.Info, text = order.customerMics)

                // Customer Phone
                InfoRow(icon = Icons.Default.Phone, text = order.customerPhone)

                // Employee ID
                InfoRow(icon = Icons.Default.CoPresent, text = order.employeeId)
            }
        }
    }

    @Composable
    fun InfoRow(icon: ImageVector, text: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f) // Allows text to take remaining space
            )
        }
    }

}
