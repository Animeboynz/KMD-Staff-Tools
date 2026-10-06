package com.animeboynz.kmd.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.presentation.Screen
import org.koin.compose.koinInject

class ManageBoxesScreen : Screen() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val repository = koinInject<StockCountRepository>()
        val screenModel = rememberScreenModel { ManageBoxesScreenModel(repository) }

        val locations by screenModel.locations.collectAsState(emptyList())
        val boxes by screenModel.boxes.collectAsState(emptyList())
        val message by screenModel.message.collectAsState()

        var locationFilterId by remember { mutableStateOf<Long?>(null) }
        var showAdd by remember { mutableStateOf(false) }
        val now = remember { System.currentTimeMillis() }

        LaunchedEffect(message) {
            val text = message ?: return@LaunchedEffect
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            screenModel.consumeMessage()
        }

        val visibleBoxes = boxes.filter { box ->
            val filterId = locationFilterId ?: return@filter true
            box.locationId in locationAndDescendants(filterId, locations)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text("Manage boxes") },
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showAdd = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add box") },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 88.dp),
            ) {
                LocationPicker(
                    label = "Location",
                    locations = locations,
                    selectedId = locationFilterId,
                    allowNone = true,
                    noneLabel = "All locations",
                    onSelected = { locationFilterId = it },
                )
                if (locations.isEmpty()) {
                    Text(
                        "Add locations in Settings, under Location.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else if (visibleBoxes.isEmpty()) {
                    Text(
                        if (locationFilterId == null) "No boxes yet." else "No boxes in this location.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                for (box in visibleBoxes) {
                    BoxRow(box, locations, now) {
                        navigator.push(StockManagementScreen(box.barcode))
                    }
                }
            }
        }

        if (showAdd) {
            AddBoxDialog(
                locations = locations,
                onDismiss = { showAdd = false },
                onConfirm = { barcode, locationId ->
                    screenModel.addBox(barcode, locationId)
                    showAdd = false
                },
            )
        }
    }
}

@Composable
private fun BoxRow(
    box: BoxEntity,
    locations: List<LocationEntity>,
    now: Long,
    onClick: () -> Unit,
) {
    val locationName = locations.find { it.id == box.locationId }
        ?.let { locationPath(it, locations) }
        ?: "Unknown location"
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(box.barcode, style = MaterialTheme.typography.titleMedium)
            Text(locationName, style = MaterialTheme.typography.bodyMedium)
            Text(daysSinceConfirm(box.lastCountedAt, now), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBoxDialog(
    locations: List<LocationEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?) -> Unit,
) {
    var barcode by remember { mutableStateOf("") }
    var locationId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add box") },
        text = {
            Column {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Box barcode") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                LocationPicker(
                    label = "Location",
                    locations = locations,
                    selectedId = locationId,
                    allowNone = false,
                    noneLabel = "Choose a location",
                    onSelected = { locationId = it },
                    horizontalPadding = 0.dp,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(barcode, locationId) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPicker(
    label: String,
    locations: List<LocationEntity>,
    selectedId: Long?,
    allowNone: Boolean,
    noneLabel: String,
    onSelected: (Long?) -> Unit,
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = when {
        selectedId == null -> noneLabel
        else -> locations.find { it.id == selectedId }?.let { locationPath(it, locations) } ?: noneLabel
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (allowNone) {
                DropdownMenuItem(
                    text = { Text(noneLabel) },
                    onClick = {
                        onSelected(null)
                        expanded = false
                    },
                )
            }
            locations.sortedBy { locationPath(it, locations) }.forEach { location ->
                DropdownMenuItem(
                    text = { Text(locationPath(location, locations)) },
                    onClick = {
                        onSelected(location.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun locationAndDescendants(rootId: Long, locations: List<LocationEntity>): Set<Long> {
    val children = locations.groupBy { it.parentId }
    val ids = mutableSetOf<Long>()
    fun walk(id: Long) {
        if (!ids.add(id)) return
        children[id].orEmpty().forEach { walk(it.id) }
    }
    walk(rootId)
    return ids
}

private fun locationPath(location: LocationEntity, locations: List<LocationEntity>): String {
    val byId = locations.associateBy { it.id }
    val parts = ArrayDeque<String>()
    val seen = mutableSetOf<Long>()
    var current: LocationEntity? = location
    while (current != null && seen.add(current.id)) {
        parts.addFirst(current.name)
        current = current.parentId?.let { byId[it] }
    }
    return parts.joinToString(" / ")
}

private fun daysSinceConfirm(lastCountedAt: Long?, now: Long): String {
    if (lastCountedAt == null) return "Never confirmed"
    val days = ((now - lastCountedAt) / MILLIS_PER_DAY).coerceAtLeast(0)
    return when (days) {
        0L -> "Confirmed today"
        1L -> "1 day since confirm"
        else -> "$days days since confirm"
    }
}

private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
