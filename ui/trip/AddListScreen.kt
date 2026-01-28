package com.example.tripshare.ui.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tripshare.data.model.SavedChecklistEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListScreen(
    onClose: () -> Unit,
    onAddCategories: (List<String>) -> Unit,
    yourLists: List<SavedChecklistEntity> = emptyList(),
    onSelectSavedList: (SavedChecklistEntity) -> Unit = {},
    onDeleteSavedList: (SavedChecklistEntity) -> Unit = {}
) {
    var tabIndex by remember { mutableStateOf(0) } // 0 = Templates, 1 = Your Lists

    // State to track which list is currently being deleted (for the Dialog)
    var listToDelete by remember { mutableStateOf<SavedChecklistEntity?>(null) }

    var templates by remember {
        mutableStateOf(
            listOf(
                CatTemplate("Hiking"),
                CatTemplate("Toiletries"),
                CatTemplate("Winter Sports"),
                CatTemplate("Gym"),
                CatTemplate("Camping"),
                CatTemplate("Beach"),
                CatTemplate("Make-up"),
                CatTemplate("Carry on"),
            )
        )
    }

    val selectedCount = templates.count { it.selected }
    val selectedNames = templates.filter { it.selected }.map { it.name }

    // --- Delete Confirmation Dialog ---
    if (listToDelete != null) {
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            title = { Text("Delete List?") },
            text = { Text("Are you sure you want to delete '${listToDelete?.name}'? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        listToDelete?.let { onDeleteSavedList(it) }
                        listToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { listToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close") }
                },
                title = { Text("Add list") },
            )
        },
        bottomBar = {
            // Only show the "Add" button if we are on the Template tab (0)
            if (tabIndex == 0) {
                Surface(tonalElevation = 3.dp) {
                    Button(
                        onClick = { onAddCategories(selectedNames) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        enabled = selectedCount > 0
                    ) {
                        Text("Add $selectedCount ${if (selectedCount == 1) "category" else "categories"}")
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            // Tabs
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Templates") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Your Lists") })
            }

            if (tabIndex == 0) {
                // --- TEMPLATES TAB ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CreateCustomListCard(onCreate = { newName ->
                            templates = templates.toMutableList().apply {
                                add(0, CatTemplate(newName, selected = true))
                            }
                        })
                    }

                    items(templates, key = { it.name }) { cat ->
                        if (cat.selected) {
                            SelectedTemplateCard(
                                name = cat.name,
                                leading = { Icon(templateIcon(cat.name), null) },
                                onRemove = {
                                    templates = templates.map { if (it.name == cat.name) it.copy(selected = false) else it }
                                }
                            )
                        } else {
                            UnselectedTemplateCard(
                                name = cat.name,
                                leading = { Icon(templateIcon(cat.name), null) },
                                onSelect = {
                                    templates = templates.map { if (it.name == cat.name) it.copy(selected = true) else it }
                                }
                            )
                        }
                    }
                }
            } else {
                // --- YOUR LISTS TAB ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (yourLists.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No saved lists yet",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(yourLists, key = { it.id }) { savedEntity ->
                            UnselectedTemplateCard(
                                name = savedEntity.name,
                                leading = { Icon(templateIcon(savedEntity.name), null) },
                                onSelect = { onSelectSavedList(savedEntity) },
                                onLongClick = { listToDelete = savedEntity } // Trigger Dialog
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Cards ---------- */

@Composable
fun CreateCustomListCard(
    onCreate: (String) -> Unit,
    suggestedName: String = "Custom list"
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        onClick = { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Create custom list",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showDialog) {
        NameDialog(
            title = "Name your list",
            initial = suggestedName,
            confirmLabel = "Create",
            onCancel = { showDialog = false },
            onConfirm = { name ->
                val trimmed = name.trim()
                if (trimmed.isNotEmpty()) onCreate(trimmed)
                showDialog = false
            }
        )
    }
}


@Composable
private fun SelectedTemplateCard(
    name: String,
    leading: @Composable () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Done, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.width(12.dp))
            leading()
            Spacer(Modifier.width(10.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UnselectedTemplateCard(
    name: String,
    leading: @Composable () -> Unit,
    onSelect: () -> Unit,
    onLongClick: (() -> Unit)? = null // Add optional long click
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { /* Do nothing, let user click the specific 'Select' button */ },
                onLongClick = onLongClick
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outline), CircleShape),
                contentAlignment = Alignment.Center
            ) {}
            Spacer(Modifier.width(12.dp))
            leading()
            Spacer(Modifier.width(10.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSelect) { Text("Select") }
        }
    }
}

/* ---------- Model + icon helper ---------- */

private data class CatTemplate(
    val name: String,
    val selected: Boolean = false
)

@Composable
private fun templateIcon(name: String) = when (name.lowercase()) {
    "hiking" -> Icons.Outlined.Hiking
    "toiletries" -> Icons.Outlined.Luggage
    "winter sports" -> Icons.Outlined.FitnessCenter
    "gym" -> Icons.Outlined.FitnessCenter
    "camping" -> Icons.Outlined.Hiking
    "beach" -> Icons.Outlined.BeachAccess
    "make-up", "make up", "makeup" -> Icons.Outlined.Face
    "carry on", "carry-on" -> Icons.Outlined.Luggage
    else -> Icons.Outlined.Luggage
}

@Composable
private fun NameDialog(
    title: String,
    initial: String,
    confirmLabel: String,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    val canSave = text.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = { Text("List name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }, enabled = canSave) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}