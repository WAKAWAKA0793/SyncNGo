package com.example.tripshare.ui.expense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tripshare.data.model.PaymentStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ExpensePaymentUi(
    val expensePaymentId: Long,
    val title: String,
    val amount: Double,
    val paymentStatus: PaymentStatus,
    val category: String,
    val effectiveOn: LocalDate,
    val dueOn: LocalDate?,
    val paidOn: LocalDate?,
    val payerId: Long,
    val payerName: String,
    val payerPhotoUrl: String?
)

@Composable
fun BudgetScreen(
    vm: ExpenseViewModel,
    tripId: Long,
    tripName: String,
    navController: NavController,
    onBack: () -> Unit,
    onOpenBalance: () -> Unit = {},
    onOpenEditExpense: (Long) -> Unit = {},
    onAddExpense: (Long) -> Unit = {}
) {
    val payments by vm.expenses.collectAsState()
    var selectedExpenseId by remember { mutableStateOf<Long?>(null) }

    val expensesByDate = remember(payments) {
        payments
            .groupBy { it.effectiveOn }
            .toSortedMap(compareByDescending { it })
    }

    // Main Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp)
        ) {

            // header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Column {
                            Text(
                                text = "Expense",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = tripName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(0.dp))
                }

                Spacer(Modifier.height(12.dp))
            }

            // grouped expenses by date
            expensesByDate.forEach { (date, listForThatDate) ->

                // date header
                item(key = "header_$date") {
                    Text(
                        text = date.formatAsPrettyHeader(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp, top = 4.dp)
                    )
                }

                val (paidList, pendingList) = listForThatDate.partition {
                    it.paymentStatus == PaymentStatus.PAID
                }

                // Pending first
                if (pendingList.isNotEmpty()) {
                    items(
                        items = pendingList,
                        key = { it.expensePaymentId }
                    ) { exp ->
                        ExpenseRowItem(
                            categoryLabel = exp.category.ifBlank { "Expense" },
                            title = exp.title,
                            amountPrimary = "MYR ${exp.amount.formatMoney()}",
                            payerName = exp.payerName,
                            payerPhotoUrl = exp.payerPhotoUrl,
                            paymentStatus = exp.paymentStatus,
                            onClick = { selectedExpenseId = exp.expensePaymentId }

                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // Paid
                if (paidList.isNotEmpty()) {
                    items(
                        items = paidList,
                        key = { it.expensePaymentId }
                    ) { exp ->
                        ExpenseRowItem(
                            categoryLabel = exp.category.ifBlank { "Expense" },
                            title = exp.title,
                            amountPrimary = "MYR ${exp.amount.formatMoney()}",
                            payerName = exp.payerName,
                            payerPhotoUrl = exp.payerPhotoUrl,
                            paymentStatus = exp.paymentStatus,
                            onClick = { selectedExpenseId = exp.expensePaymentId }

                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        // bottom nav / FAB
        BottomBudgetBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            onFabClick = {
                onAddExpense(tripId)
                navController.navigate("addExpense/$tripId")
            },
            onNavExpenses = { /* already here */ },
            onNavBalance = { onOpenBalance() }
        )

        selectedExpenseId?.let { id ->
            val latest = payments.firstOrNull { it.expensePaymentId == id }
            if (latest != null) {
                ExpenseDetailDialog(
                    vm = vm,
                    expense = latest,
                    onDismiss = { selectedExpenseId = null },
                    onEdit = { expenseId ->
                        selectedExpenseId = null
                        onOpenEditExpense(expenseId)
                    }
                )
            }
        }
    }
}

/**
 * A single expense row.
 */
@Composable
private fun ExpenseRowItem(
    categoryLabel: String,
    title: String,
    amountPrimary: String,
    payerName: String,
    payerPhotoUrl: String?,
    paymentStatus: PaymentStatus,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.weight(1f)
        ) {
            PayerAvatar(name = payerName, photoUrl = payerPhotoUrl)

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    categoryLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        // Use Primary color for category label to match theme identity
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                amountPrimary,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )

            PaymentStatusChip(paymentStatus)
        }
    }
}

@Composable
private fun PaymentStatusChip(status: PaymentStatus) {
    val isPaid = (status == PaymentStatus.PAID)

    // Map Paid -> Primary (Blue in your theme), Pending -> Error (Red in your theme)
    val bgColor = if (isPaid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
    val textColor = if (isPaid) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
    val label = if (isPaid) "PAID" else "PENDING"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PayerAvatar(
    name: String,
    photoUrl: String?,
) {
    val initials = remember(name) { initialsFrom(name) }

    if (!photoUrl.isNullOrBlank()) {
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            modifier = Modifier.size(48.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
                contentDescription = "Payer avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Surface(
            shape = CircleShape,
            // Use SurfaceVariant for neutral placeholders
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

/**
 * Bottom curved nav bar with: Expenses | (+) | Balance
 */
@Composable
private fun BottomBudgetBar(
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit,
    onNavExpenses: () -> Unit,
    onNavBalance: () -> Unit,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface, // Use Theme Surface
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .heightIn(min = 72.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavExpenses() }
            ) {
                Text(
                    "Expenses",
                    // Use Primary for the "Active" tab
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Floating Action Button in the middle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    // Use PrimaryContainer for emphasis
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onFabClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add expense",
                    // Contrast content
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavBalance() }
            ) {
                Text(
                    "Balance",
                    // Use OnSurfaceVariant for the "Inactive" tab
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/* -------------------- Expense Detail Dialog -------------------- */

@Composable
fun ExpenseDetailDialog(
    vm: ExpenseViewModel,
    expense: ExpensePaymentUi,
    onDismiss: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val splits by vm
        .observeSplitsForExpense(expense.expensePaymentId)
        .collectAsState(initial = emptyList())

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                /* ───── Header ───── */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Expense details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.clickable { onDismiss() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                /* ───── Summary ───── */
                DetailRow(label = "Title", value = expense.title)
                DetailRow(label = "Category", value = expense.category)
                DetailRow(
                    label = "Amount",
                    value = "MYR ${expense.amount.formatMoney()}",
                    emphasize = true
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                /* ───── Split section ───── */
                Text(
                    "Split breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (splits.isEmpty()) {
                    Text(
                        "No split information available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        splits.forEach { s ->
                            SplitRow(
                                name = s.name,
                                photoUrl = s.photoUrl,
                                amount = s.amount
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total owed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "MYR ${splits.sumOf { it.amount }.formatMoney()}",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                /* ───── Actions ───── */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    ) { Text("Close") }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onEdit(expense.expensePaymentId) }
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            modifier = Modifier.width(90.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
private fun SplitRow(
    name: String,
    photoUrl: String?,
    amount: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Use SurfaceVariant for row background
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PayerAvatar(name = name, photoUrl = photoUrl)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Owes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            "MYR ${amount.formatMoney()}",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


/* ---------- helpers ---------- */

private fun LocalDate.formatAsPrettyHeader(): String {
    val fmt = DateTimeFormatter.ofPattern("EEE d MMMM")
    return this.format(fmt)
}

private fun Double.formatMoney(): String {
    val text = String.format("%.2f", this)
    return if (text.endsWith(".00")) text.dropLast(3) else text.trimEnd('0').trimEnd('.')
}

private fun initialsFrom(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    if (parts.isEmpty()) return "U"
    val first = parts[0].first().uppercaseChar()
    val second = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar()
    return if (second != null) "$first$second" else "$first"
}