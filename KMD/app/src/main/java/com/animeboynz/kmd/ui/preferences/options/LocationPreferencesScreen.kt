package com.animeboynz.kmd.ui.preferences.options

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.presentation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class LocationPreferencesScreenModel(
    private val stockCountRepository: StockCountRepository,
) : ScreenModel {
    val locations = stockCountRepository.getLocations()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun addLocation(name: String, parentId: Long?) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            _message.value = "Enter a location name"
            return
        }
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.insertLocation(trimmed, parentId)
            _message.value = "Location added"
        }
    }

    fun deleteLocation(id: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.deleteLocation(id)
            _message.value = "Location removed"
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

object LocationPreferencesScreen : Screen() {
    private fun readResolve(): Any = LocationPreferencesScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val repository = koinInject<StockCountRepository>()
    val screenModel = rememberScreenModel { LocationPreferencesScreenModel(repository) }
    val locations by screenModel.locations.collectAsState(emptyList())
    val message by screenModel.message.collectAsState()

    var collapsed by remember { mutableStateOf(setOf<Long>()) }
    var addParentId by remember { mutableStateOf<Long?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<LocationEntity?>(null) }

    LaunchedEffect(message) {
        val text = message ?: return@LaunchedEffect
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        screenModel.consumeMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_location_title)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
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
        Button(
            onClick = {
                addParentId = null
                showAdd = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Text("Add location")
        }
        if (locations.isEmpty()) {
            Text(
                "No locations yet. Add a store area, then add the rooms and bays inside it.",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            val children = locations.groupBy { it.parentId }
            for (root in locations.filter { it.parentId == null }.sortedBy { it.name }) {
                LocationBranch(
                    location = root,
                    depth = 0,
                    children = children,
                    collapsed = collapsed,
                    onToggle = { id ->
                        collapsed = if (id in collapsed) collapsed - id else collapsed + id
                    },
                    onAddChild = { parent ->
                        addParentId = parent.id
                        showAdd = true
                    },
                    onDelete = { pendingDelete = it },
                )
            }
        }
    }

    if (showAdd) {
        AddLocationDialog(
            locations = locations,
            initialParentId = addParentId,
            onDismiss = { showAdd = false },
            onConfirm = { name, parentId ->
                screenModel.addLocation(name, parentId)
                showAdd = false
            },
        )
    }

    pendingDelete?.let { location ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Remove location") },
            text = {
                Text("Remove ${location.name} and every location inside it? Boxes stored there are removed too.")
            },
            confirmButton = {
                TextButton(onClick = {
                    screenModel.deleteLocation(location.id)
                    pendingDelete = null
                }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }
    }
    }
}

@Composable
private fun LocationBranch(
    location: LocationEntity,
    depth: Int,
    children: Map<Long?, List<LocationEntity>>,
    collapsed: Set<Long>,
    onToggle: (Long) -> Unit,
    onAddChild: (LocationEntity) -> Unit,
    onDelete: (LocationEntity) -> Unit,
) {
    val nested = children[location.id].orEmpty().sortedBy { it.name }
    val expanded = location.id !in collapsed
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (8 + depth * 20).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (nested.isEmpty()) {
            Spacer(Modifier.width(40.dp))
        } else {
            IconButton(onClick = { onToggle(location.id) }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                )
            }
        }
        Text(
            text = location.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { onAddChild(location) }) {
            Icon(Icons.Default.Add, contentDescription = "Add location inside")
        }
        IconButton(onClick = { onDelete(location) }) {
            Icon(Icons.Default.Delete, contentDescription = "Remove location")
        }
    }
    if (expanded) {
        for (child in nested) {
            LocationBranch(
                location = child,
                depth = depth + 1,
                children = children,
                collapsed = collapsed,
                onToggle = onToggle,
                onAddChild = onAddChild,
                onDelete = onDelete,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLocationDialog(
    locations: List<LocationEntity>,
    initialParentId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var parentId by remember { mutableStateOf(initialParentId) }
    var expanded by remember { mutableStateOf(false) }
    val parentLabel = locations.find { it.id == parentId }?.let { locationPath(it, locations) } ?: "None"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add location") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    OutlinedTextField(
                        value = parentLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Inside") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                parentId = null
                                expanded = false
                            },
                        )
                        locations.sortedBy { locationPath(it, locations) }.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(locationPath(location, locations)) },
                                onClick = {
                                    parentId = location.id
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, parentId) }) {
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
