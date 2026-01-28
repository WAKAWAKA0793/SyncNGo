package com.example.tripshare.ui.trip

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tripshare.data.model.TripEntity
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    vm: ChecklistViewModel,
    currentTripName: String,
    currentTripId: Long,
    onBack: () -> Unit,
    onSwitchTrip: (Long) -> Unit,
    onOpenCategory: (CategoryUi) -> Unit,
    onOpenAddLists: () -> Unit
) {
    val categories by vm.categories.collectAsStateWithLifecycle(emptyList<CategoryUi>())
    val joinedTrips by vm.joinedTrips.collectAsStateWithLifecycle()
    // 1. Observe Saved Lists
    val savedLists by vm.savedLists.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val (doneAll, totalAll) = remember(categories) {
        val d = categories.sumOf { it.done }
        val t = categories.sumOf { it.total.coerceAtLeast(0) }
        d to t
    }
    val progress = if (totalAll == 0) 0f else doneAll.toFloat() / totalAll

    Scaffold(
        topBar = {
            SmallTopBarWithDropdown(
                currentTripName = currentTripName,
                joinedTrips = joinedTrips,
                currentTripId = currentTripId,
                onBack = onBack,
                onTripSelected = onSwitchTrip,
                onDeleteChecklist = vm::deleteChecklist,

                // 👇 Wire up the two share actions
                onSharePdf = {
                    vm.shareChecklistToChat(context)
                    Toast.makeText(context, "Sending PDF...", Toast.LENGTH_SHORT).show()
                },
                onShareJson = {
                    vm.shareChecklistJsonToChat()
                    Toast.makeText(context, "Sending Editable List...", Toast.LENGTH_SHORT).show()
                }
            )

        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenAddLists) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 0.dp,
                end = 16.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProgressRow(
                    percent = (progress * 100).coerceIn(0f, 100f).roundToInt(),
                    progress = progress
                )
            }

            // 2. Display "Your Lists" if any exist
            if (savedLists.isNotEmpty()) {
                item {
                    Text(
                        "Your Lists",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(savedLists, key = { it.id }) { list ->
                            AssistChip(
                                onClick = { vm.addSavedListToTrip(list) },
                                label = { Text(list.name) },
                                leadingIcon = { Icon(Icons.Outlined.Checklist, null) },
                                shape = RoundedCornerShape(24.dp)
                            )
                        }
                    }
                }
            }

            // 3. Suggestions Section
            item {
                Text(
                    "Suggestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                SuggestionChips(
                    suggestions = listOf("Hiking", "Winter Sports", "Gym", "Camping"),
                    onAddTemplate = vm::onAddTemplate
                )
            }

            items(categories.chunked(2), key = { chunk ->
                chunk.firstOrNull()?.categoryId ?: -1L
            }) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CategoryCardGrid(
                        category = rowItems[0],
                        onClick = { onOpenCategory(rowItems[0]) },
                        onDelete = { vm.deleteCategory(rowItems[0].categoryId) },
                        modifier = Modifier.weight(1f)
                    )
                    if (rowItems.size > 1) {
                        CategoryCardGrid(
                            category = rowItems[1],
                            onClick = { onOpenCategory(rowItems[1]) },
                            onDelete = { vm.deleteCategory(rowItems[1].categoryId) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }

        }
    }
}


/* -------------------- Top App Bar with Dropdown -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallTopBarWithDropdown(
    currentTripName: String,
    joinedTrips: List<TripEntity>,
    currentTripId: Long,
    onBack: () -> Unit,
    onTripSelected: (Long) -> Unit,
    onDeleteChecklist: () -> Unit,
    onSharePdf: () -> Unit,
    onShareJson: () -> Unit
) {
    var tripMenuExpanded by remember { mutableStateOf(false) }
    var optionsMenuExpanded by remember { mutableStateOf(false) }
    var shareMenuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Column {
                Text(
                    "Checklist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { tripMenuExpanded = true }
                        .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
                ) {
                    Text(
                        text = currentTripName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Trip",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    DropdownMenu(
                        expanded = tripMenuExpanded,
                        onDismissRequest = { tripMenuExpanded = false }
                    ) {
                        if (joinedTrips.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No other trips") },
                                onClick = { tripMenuExpanded = false },
                                enabled = false
                            )
                        } else {
                            joinedTrips.forEach { trip ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = trip.name,
                                            fontWeight = if (trip.id == currentTripId) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        tripMenuExpanded = false
                                        if (trip.id != currentTripId) {
                                            onTripSelected(trip.id)
                                        }
                                    },
                                    trailingIcon = if (trip.id == currentTripId) {
                                        { Icon(Icons.Default.Check, contentDescription = null) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        },
        actions = {
            // 👇 UPDATED: Share Button with Dropdown
            Box {
                IconButton(onClick = { shareMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
                DropdownMenu(
                    expanded = shareMenuExpanded,
                    onDismissRequest = { shareMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Share as PDF") },
                        onClick = {
                            shareMenuExpanded = false
                            onSharePdf()
                        },
                        leadingIcon = { Icon(Icons.Default.Share, null) }
                    )
                }
            }
            Box {
                IconButton(onClick = { optionsMenuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
                DropdownMenu(
                    expanded = optionsMenuExpanded,
                    onDismissRequest = { optionsMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete checklist") },
                        onClick = {
                            optionsMenuExpanded = false
                            onDeleteChecklist()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }
        }
    )
}

/* -------------------- Progress header -------------------- */

@Composable
private fun ProgressRow(percent: Int, progress: Float) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("$percent%", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/* -------------------- Suggestion Chips -------------------- */

@Composable
private fun SuggestionChips(
    suggestions: List<String>,
    onAddTemplate: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(suggestions, key = { it }) { label ->
            AssistChip(
                onClick = { onAddTemplate(label) },
                label = { Text(label, maxLines = 1) },
                leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

/* -------------------- Category Card (Grid style) -------------------- */
@Composable
private fun CategoryCardGrid(
    category: CategoryUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        modifier = modifier.aspectRatio(1f) // keeps it square
    ) {
        // 1. Root Box allows overlapping (Menu on top of Content)
        Box(modifier = Modifier.fillMaxSize()) {

            // --- A. Top Right Menu ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp) // Slight padding from the corner
            ) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Category options"
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete category") },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }

            // --- B. Main Content (Centered) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Center content vertically
            ) {
                // Donut & Icon
                Box(contentAlignment = Alignment.Center) {
                    val progress = if (category.total == 0) 0f else category.done.toFloat() / category.total
                    DonutProgress(progress = progress)
                    Icon(
                        imageVector = categoryIcon(category.title),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Title
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Count
                Text(
                    text = "${category.done} / ${category.total}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


/* Donut progress that matches the “thin ring with colored arc” vibe */
@Composable
private fun DonutProgress(progress: Float) {
    // background ring
    CircularProgressIndicator(
        progress = { 1f },
        modifier = Modifier.size(56.dp),
        strokeWidth = 6.dp,
        strokeCap = StrokeCap.Round,
        color = MaterialTheme.colorScheme.surfaceVariant
    )
    // foreground arc
    CircularProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier.size(56.dp),
        strokeWidth = 6.dp,
        strokeCap = StrokeCap.Round
    )
}

/* Pick a friendly icon per category name (fallback to checklist) */
@Composable
private fun categoryIcon(title: String) = when (title.trim().lowercase()) {
    "clothing", "clothes" -> Icons.Outlined.Luggage
    "essentials", "emergency" -> Icons.Outlined.HealthAndSafety
    "toiletries" -> Icons.Outlined.WaterDrop
    "laundry" -> Icons.Outlined.LocalLaundryService
    else -> Icons.Outlined.Checklist
}

/* -------------------- Category Detail Screen -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    vm: ChecklistViewModel,
    categoryId: Long,
    onBack: () -> Unit
) {
    val categories by vm.categories.collectAsStateWithLifecycle(emptyList<CategoryUi>())
    val category = categories.firstOrNull { it.categoryId == categoryId }

    if (category == null) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Checklist") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }) { inner ->
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Category not found")
            }
        }
        return
    }

    // Observe saved lists to check if already saved
    val savedLists by vm.savedLists.collectAsStateWithLifecycle()

    // Check if the list name already exists in the saved database
    val isSaved = remember(savedLists, category.title) {
        savedLists.any { it.name.equals(category.title, ignoreCase = true) }
    }

    val progress = if (category.total == 0) 0f else category.done.toFloat() / category.total
    val percent = (progress * 100).toInt()

    var newTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                categoryIcon(category.title),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                category.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            "Individual list",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // 🔖 UPDATED: Check !isSaved before saving
                    IconButton(
                        onClick = {
                            if (!isSaved) {
                                vm.saveCategoryToMyLists(categoryId)
                            }
                            // Optional: If isSaved is true, you could show a Toast here saying "Already saved"
                        }
                    ) {
                        Icon(
                            imageVector = if (isSaved)
                                Icons.Filled.Bookmark
                            else
                                Icons.Filled.BookmarkBorder,
                            // Visual feedback: Turn it Primary color if saved
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            contentDescription = if (isSaved)
                                "Saved to My Lists"
                            else
                                "Save to My Lists"
                        )
                    }
                    IconButton(onClick = { /* sort */ }) {
                        Icon(Icons.Default.UnfoldMore, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            // Add item bar
            Surface(tonalElevation = 3.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add item..") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    FilledIconButton(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                vm.addItem(
                                    categoryId,
                                    newTitle.trim(),
                                    dueDate = null,
                                    note = null
                                )
                                newTitle = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // progress row
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$percent%",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            items(category.items, key = { it.id }) { row ->
                CategoryDetailRow(
                    item = row,
                    onToggle = { toggled -> vm.onToggle(toggled) },
                    onQuantityChange = { changed, newQty ->
                        vm.setQuantity(changed, newQty)
                    },
                    onDelete = { vm.deleteItem(row.id) }   // 👈 swipe will call this
                )
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDetailRow(
    item: ItemUi,
    onToggle: (ItemUi) -> Unit,
    onQuantityChange: (ItemUi, Int) -> Unit,
    onDelete: () -> Unit
) {
    val checked = item.completed
    val animatedAlpha by animateFloatAsState(if (checked) 0.6f else 1f, label = "alpha")

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            // FIX: Removed "direction ->" argument.
            // We access the direction directly from the state instead.
            val direction = dismissState.dismissDirection

            val color = MaterialTheme.colorScheme.errorContainer
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.CenterEnd
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .toggleable(
                        value = checked,
                        role = Role.Checkbox,
                        onValueChange = { onToggle(item) }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TickBubble(checked = checked, onClick = { onToggle(item) })
                Spacer(Modifier.width(12.dp))
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = animatedAlpha),
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val newQty = (item.quantity - 1).coerceAtLeast(0)
                                if (newQty != item.quantity) onQuantityChange(item, newQty)
                            },
                            modifier = Modifier.size(28.dp)
                        ) { Icon(Icons.Default.Remove, "Decrease") }

                        Text(
                            "${item.quantity}x",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChange(item, item.quantity + 1) },
                            modifier = Modifier.size(28.dp)
                        ) { Icon(Icons.Default.Add, "Increase") }
                    }
                }
            }
        }
    )
}

@Composable
private fun TickBubble(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
    val border = if (checked) null else BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bg,
        border = border,
        tonalElevation = 0.dp,
        modifier = modifier.size(28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}