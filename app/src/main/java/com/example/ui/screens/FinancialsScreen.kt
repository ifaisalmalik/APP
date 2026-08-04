package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.FarmhouseExpense
import com.example.data.model.ManagerUser
import com.example.ui.theme.AmberPending
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinancialsScreen(
    bookings: List<Booking>,
    expenses: List<FarmhouseExpense>,
    activeManager: ManagerUser,
    onAddExpense: (title: String, category: String, amount: Double, notes: String) -> Unit,
    onDeleteExpense: (FarmhouseExpense) -> Unit,
    modifier: Modifier = Modifier
) {
    var showExpenseDialog by remember { mutableStateOf(false) }

    val activeBookings = bookings.filter { it.status != "CANCELLED" }
    val totalRevenue = activeBookings.sumOf { it.totalAmount }
    val totalAdvance = activeBookings.sumOf { it.advanceAmount }
    val totalPending = activeBookings.sumOf { it.remainingAmount }
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpenses

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Main Financial Balance Overview Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("NET PROFIT / CASHFLOW", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text(
                                text = "PKR ${netProfit.toInt()}",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, null, tint = GoldPrimary, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakdown Metrics Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FinancialMetricBox(label = "Total Booking Value", amount = totalRevenue, color = TextPrimary)
                        FinancialMetricBox(label = "Advance Collected", amount = totalAdvance, color = EmeraldGreen)
                        FinancialMetricBox(label = "Pending Receivables", amount = totalPending, color = AmberPending)
                    }
                }
            }
        }

        // Operational Expenses Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, null, tint = CoralRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("FARMHOUSE MAINTENANCE EXPENSES", color = CoralRed, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                }

                Surface(
                    onClick = { showExpenseDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = CoralRed.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, tint = CoralRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Expense", color = CoralRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        if (expenses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No maintenance expenses logged yet.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(expenses) { expense ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${expense.category} • ${expense.getFormattedDate()}", color = TextSecondary, fontSize = 12.sp)
                            Text("Logged by: ${expense.loggedByManagerName}", color = GoldPrimary, fontSize = 10.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PKR ${expense.amount.toInt()}",
                                color = CoralRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            IconButton(onClick = { onDeleteExpense(expense) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showExpenseDialog) {
        LogExpenseSheet(
            activeManager = activeManager,
            onSave = { title, cat, amt, notes ->
                onAddExpense(title, cat, amt, notes)
                showExpenseDialog = false
            },
            onDismiss = { showExpenseDialog = false }
        )
    }
}

@Composable
private fun FinancialMetricBox(label: String, amount: Double, color: Color) {
    Column {
        Text(label, color = TextMuted, fontSize = 10.sp)
        Text(
            text = "PKR ${amount.toInt()}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogExpenseSheet(
    activeManager: ManagerUser,
    onSave: (title: String, category: String, amount: Double, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pool Maintenance") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Pool Maintenance", "Generator Fuel", "Staff Salary", "Repairs", "Utilities")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("Log Farmhouse Expense", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Manager: ${activeManager.name}", color = GoldPrimary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Title (e.g. Diesel 100L)") },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoralRed, focusedLabelColor = CoralRed, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Amount (PKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoralRed, focusedLabelColor = CoralRed, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Category:", color = TextSecondary, fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.take(3).forEach { cat ->
                    Surface(
                        onClick = { category = cat },
                        shape = RoundedCornerShape(16.dp),
                        color = if (category == cat) CoralRed else DarkSurfaceVariant
                    ) {
                        Text(cat, color = if (category == cat) Color.White else TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onSave(title, category, amt, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Log Expense Item", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
