package com.animeboynz.kmd.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.tools.ToolRow
import org.koin.compose.koinInject

class BoxInventoryHubScreen : Screen() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val repository = koinInject<StockCountRepository>()
        val screenModel = rememberScreenModel { BoxInventoryHubScreenModel(repository) }

        val locations by screenModel.locations.collectAsState(emptyList())
        val boxes by screenModel.boxes.collectAsState(emptyList())
        val message by screenModel.message.collectAsState()

        var locationName by remember { mutableStateOf("") }
        var parentId by remember { mutableStateOf<Long?>(null) }
        var boxBarcode by remember { mutableStateOf("") }
        var boxLocationId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(message) {
            val text = message ?: return@LaunchedEffect
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            screenModel.consumeMessage()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text("Box inventory") },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
            ) {
                ToolRow("Search stock") { navigator.push(StockSearchScreen()) }
                ToolRow("Scan a box") { navigator.push(StockManagementScreen()) }
                ToolRow("Import stock") { navigator.push(StockImportScreen()) }

                Text(
                    text = "Locations",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Location name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                LocationPicker(
                    label = "Inside",
                    locations = locations,
                    selectedId = parentId,
                    allowNone = true,
                    onSelected = { parentId = it },
                )
                Button(
                    onClick = {
                        screenModel.addLocation(locationName, parentId)
                        locationName = ""
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text("Add location")
                }
                if (locations.isEmpty()) {
                    Text(
                        "No locations yet. Add a backroom or bay before creating a box.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    locations.forEach { location ->
                        val parent = locations.find { it.id == location.parentId }?.name
                        val label = if (parent == null) location.name else "$parent / ${location.name}"
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                }

                Text(
                    text = "Boxes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
                OutlinedTextField(
                    value = boxBarcode,
                    onValueChange = { boxBarcode = it },
                    label = { Text("Box barcode") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                LocationPicker(
                    label = "Location",
                    locations = locations,
                    selectedId = boxLocationId,
                    allowNone = false,
                    onSelected = { boxLocationId = it },
                )
                Button(
                    onClick = {
                        screenModel.addBox(boxBarcode, boxLocationId)
                        boxBarcode = ""
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text("Add box")
                }
                boxes.forEach { box ->
                    val locationName = locations.find { it.id == box.locationId }?.name ?: "Unknown location"
                    Text(
                        text = "${box.barcode} · $locationName",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPicker(
    label: String,
    locations: List<LocationEntity>,
    selectedId: Long?,
    allowNone: Boolean,
    onSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = when {
        selectedId == null && allowNone -> "None"
        selectedId == null -> "Choose a location"
        else -> locations.find { it.id == selectedId }?.name ?: "Choose a location"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    text = { Text("None") },
                    onClick = {
                        onSelected(null)
                        expanded = false
                    },
                )
            }
            locations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location.name) },
                    onClick = {
                        onSelected(location.id)
                        expanded = false
                    },
                )
            }
        }
    }
}
