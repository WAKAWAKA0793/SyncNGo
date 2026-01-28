// ui/group/TripVoteScreen.kt
package com.example.tripshare.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.PollEntity
import com.example.tripshare.data.model.PollVoterDetail
import com.example.tripshare.data.model.VoteOptionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripVoteScreen(
    voteVm: VoteViewModel,
    onBack: () -> Unit
) {
    val polls by voteVm.polls.collectAsState()
    val context = LocalContext.current
    val myUserId by AuthPrefs.getUserId(context).collectAsState(initial = -1L)

    // State for viewing details
    var pollToViewDetails by remember { mutableStateOf<PollEntity?>(null) }
    var optionsForDetails by remember { mutableStateOf<List<VoteOptionEntity>>(emptyList()) }

    // State for creating a new poll
    var showCreateDialog by remember { mutableStateOf(false) }

    // State for editing an existing poll
    var pollToEdit by remember { mutableStateOf<PollEntity?>(null) }
    var optionsForEdit by remember { mutableStateOf<List<String>>(emptyList()) }

    val sortedPolls = remember(polls) { polls.sortedByDescending { it.id } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Trip Polls",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create poll")
            }
        }
    ) { innerPadding ->

        if (myUserId == -1L) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }
        val userId = myUserId!!

        if (polls.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(
                    "No polls yet. Tap + to create one.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                items(sortedPolls, key = { it.id }) { poll ->
                    val optionsEntities by voteVm.getOptions(poll.id).collectAsState(initial = emptyList())

                    BluePollCard(
                        poll = poll,
                        options = optionsEntities.map { it.option },
                        voteCounts = optionsEntities.map { it.votes },
                        optionIds = optionsEntities.map { it.id },
                        onVote = { optId -> voteVm.vote(poll, optId, userId) },
                        onEdit = {
                            optionsForEdit = optionsEntities.map { it.option }
                            pollToEdit = poll
                        },
                        onDelete = { voteVm.deletePoll(poll.id) },
                        onViewVotes = {
                            optionsForDetails = optionsEntities
                            pollToViewDetails = poll
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // Dialogs
    // ─────────────────────────────────────────────────────────

    if (showCreateDialog) {
        PollDialog(
            title = "Create Poll",
            confirmText = "Create",
            initialQuestion = "",
            initialOptions = listOf("", ""),
            initialAllowMultiple = false,
            onConfirm = { q, o, m ->
                voteVm.createPoll(q, o, m)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    if (pollToEdit != null) {
        PollDialog(
            title = "Edit Poll",
            confirmText = "Save",
            initialQuestion = pollToEdit!!.question,
            initialOptions = optionsForEdit,
            initialAllowMultiple = pollToEdit!!.allowMultiple,
            onConfirm = { q, o, m ->
                voteVm.updatePoll(pollToEdit!!.id, q, o, m)
                pollToEdit = null
            },
            onDismiss = { pollToEdit = null }
        )
    }

    if (pollToViewDetails != null) {
        PollVotersDialog(
            poll = pollToViewDetails!!,
            options = optionsForDetails,
            voteVm = voteVm,
            onDismiss = { pollToViewDetails = null }
        )
    }
}

// ─────────────────────────────────────────────────────────
// 🎨 Themed Components
// ─────────────────────────────────────────────────────────

@Composable
private fun BluePollCard(
    poll: PollEntity,
    options: List<String>,
    voteCounts: List<Int>,
    optionIds: List<Long>,
    onVote: (Long) -> Unit,
    onViewVotes: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val totalVotes = voteCounts.sum()
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ─── Header Row: Question + Menu ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = poll.question,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Selection Type Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (poll.allowMultiple) "Select multiple" else "Select one",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options List
            options.forEachIndexed { index, optionText ->
                val votes = voteCounts[index]
                val id = optionIds[index]
                val progress = if (totalVotes > 0) votes / totalVotes.toFloat() else 0f
                val isSelected = false // Logic for selection state if available

                BluePollOptionRow(
                    label = optionText,
                    voteCount = votes,
                    progress = progress,
                    isSelected = isSelected,
                    onClick = { onVote(id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Footer
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View votes",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clickable { onViewVotes() }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun BluePollOptionRow(
    label: String,
    voteCount: Int,
    progress: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // A. Checkbox Indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // B. Option Text
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // C. Vote Count
            if (voteCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = voteCount.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // D. Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(start = 34.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round,
        )
    }
}

// ─────────────────────────────────────────────────────────
// Dialog (Themed)
// ─────────────────────────────────────────────────────────
@Composable
private fun PollDialog(
    title: String,
    confirmText: String,
    initialQuestion: String = "",
    initialOptions: List<String> = listOf("", ""),
    initialAllowMultiple: Boolean = false,
    onConfirm: (question: String, options: List<String>, allowMultiple: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var question by remember { mutableStateOf(initialQuestion) }
    val options = remember {
        mutableStateListOf<String>().apply {
            if (initialOptions.isEmpty()) {
                add(""); add("")
            } else {
                addAll(initialOptions)
            }
        }
    }
    var allowMultiple by remember { mutableStateOf(initialAllowMultiple) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text("Options", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)

                options.forEachIndexed { index, option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = option,
                            onValueChange = { newText -> options[index] = newText },
                            label = { Text("Option ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        if (options.size > 2) {
                            IconButton(onClick = { options.removeAt(index) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                TextButton(
                    onClick = { options.add("") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("+ Add option", color = MaterialTheme.colorScheme.primary)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = allowMultiple,
                        onCheckedChange = { allowMultiple = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text("Allow multiple answers", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            val validOptions = options.map { it.trim() }.filter { it.isNotEmpty() }
            Button(
                enabled = question.isNotBlank() && validOptions.size >= 2,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = { onConfirm(question.trim(), validOptions, allowMultiple) }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun PollVotersDialog(
    poll: PollEntity,
    options: List<VoteOptionEntity>,
    voteVm: VoteViewModel,
    onDismiss: () -> Unit
) {
    val allVoters by voteVm.getVoters(poll.id).collectAsState(initial = emptyList())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // 1. Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(
                        text = "Poll Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 2. Question
                Text(
                    text = poll.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )

                // 3. List of Options & Voters
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(options) { option ->
                        val votersForOption = allVoters.filter { it.optionId == option.id }
                        VoterSection(
                            optionText = option.option,
                            count = votersForOption.size,
                            voters = votersForOption
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoterSection(
    optionText: String,
    count: Int,
    voters: List<PollVoterDetail>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Option Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = optionText,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count votes",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        // List of Voters
        if (voters.isEmpty()) {
            Text(
                text = "No votes yet",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            voters.forEach { voter ->
                VoterRow(voter)
            }
        }
    }
}

@Composable
fun VoterRow(voter: PollVoterDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        if (voter.avatarUrl != null) {
            AsyncImage(
                model = voter.avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = voter.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}