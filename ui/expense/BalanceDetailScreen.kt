package com.example.tripshare.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceDetailScreen(
    vm: ExpenseViewModel,
    userId: Long,
    onBack: () -> Unit
) {
    val history by remember(userId) { vm.getBalanceHistoryForUser(userId) }
        .collectAsState(initial = emptyList())

    val people by vm.balancePeopleFlow.collectAsState()
    val userUi = people.find { it.id == userId }

    val bg = Color(0xFFF7FBFB)
    val textDark = Color(0xFF003D37)
    val green = Color(0xFF00A27A)
    val red = Color(0xFFD93025)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userUi?.name ?: "Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = bg
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {

            // 1. Summary Card
            if (userUi != null) {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Current Balance", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))

                        val bal = userUi.balance
                        val sign = if (bal > 0) "+" else if (bal < 0) "-" else ""
                        val color = if (bal > 0) green else if (bal < 0) red else Color.Gray

                        Text(
                            text = "$sign${String.format("%.2f", abs(bal))}",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = color
                        )
                        Spacer(Modifier.height(16.dp))

                        // Mini stats
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            StatItem(label = "Paid Total", amount = userUi.paid, color = red)
                            StatItem(label = "Fair Share", amount = userUi.fairShare, color = green)
                        }
                    }
                }
            }

            // 2. Transaction List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "History",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = textDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (history.isEmpty()) {
                    item {
                        Text("No transactions found.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                }

                items(history) { item ->
                    HistoryRow(item)
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(
            String.format("%.2f", amount),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}

@Composable
fun HistoryRow(item: BalanceHistoryItem) {
    val dateFmt = DateTimeFormatter.ofPattern("MMM dd")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Title
                val title = when(item) {
                    is BalanceHistoryItem.ExpensePaid -> "Paid for ${item.title}"
                    is BalanceHistoryItem.ExpenseShare -> "Share of ${item.title}"
                    is BalanceHistoryItem.SettlementSent -> "Sent to ${item.receiverName}"
                    is BalanceHistoryItem.SettlementReceived -> "Received from ${item.senderName}"
                }
                Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))

                // Date
                Text(
                    item.date.format(dateFmt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Amount
            // Logic:
            // Paid for expense = (+) Credit (Green)
            // Share of expense = (-) Debt (Red)
            // Sent settlement  = (+) Settled debt (Green)
            // Received settlement = (-) Settled credit (Red - technically reduces balance)

            val (amount, color, prefix) = when(item) {
                is BalanceHistoryItem.ExpensePaid -> Triple(item.amount, Color(0xFFF44336), "-")
                is BalanceHistoryItem.ExpenseShare -> Triple(item.amountOwed, Color(0xFF009688), "+")
                is BalanceHistoryItem.SettlementSent -> Triple(item.amount, Color(0xFFF44336), "-")
                is BalanceHistoryItem.SettlementReceived -> Triple(item.amount, Color(0xFF009688), "+")
            }

            Text(
                text = "$prefix${String.format("%.2f", amount)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}