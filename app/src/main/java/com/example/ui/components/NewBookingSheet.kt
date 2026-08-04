package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.ManagerUser
import com.example.ui.theme.DarkBorderColor
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBookingSheet(
    activeManager: ManagerUser,
    onSaveBooking: (Booking) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var guestName by remember { mutableStateOf("") }
    var guestPhone by remember { mutableStateOf("") }
    var guestCountStr by remember { mutableStateOf("15") }
    var totalAmountStr by remember { mutableStateOf("75000") }
    var advanceAmountStr by remember { mutableStateOf("25000") }
    var slotType by remember { mutableStateOf("FULL_DAY") }
    var notes by remember { mutableStateOf("") }

    var pendingBooking by remember { mutableStateOf<Booking?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val slots = listOf(
        "FULL_DAY" to "Full Day (24 hrs)",
        "DAY_SHIFT" to "Day Shift (10am-7pm)",
        "NIGHT_SHIFT" to "Night Shift (9pm-7am)"
    )

    if (showConfirmationDialog && pendingBooking != null) {
        val booking = pendingBooking!!
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.EventAvailable,
                    contentDescription = null,
                    tint = GoldPrimary
                )
            },
            title = {
                Text(
                    text = "Confirm Reservation",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Manager ${activeManager.name}, please verify the reservation details below to prevent accidental entries:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Guest Name: ${booking.guestName}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Phone: ${booking.guestPhone.ifBlank { "N/A" }}", color = TextPrimary, fontSize = 13.sp)
                    Text("• Guests: ${booking.guestCount} persons", color = TextPrimary, fontSize = 13.sp)
                    Text("• Total Rate: PKR ${booking.totalAmount.toInt()}", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("• Advance: PKR ${booking.advanceAmount.toInt()}", color = TextPrimary, fontSize = 13.sp)
                    Text("• Status: ${booking.status}", color = TextMuted, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        onSaveBooking(booking)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF381E72)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Finalize Reservation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Review Details", color = TextMuted)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Create New Farmhouse Booking", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Booking Manager: ${activeManager.name}", color = GoldPrimary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = guestName,
                onValueChange = { guestName = it },
                label = { Text("Guest / Host Full Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    focusedLabelColor = GoldPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = guestPhone,
                onValueChange = { guestPhone = it },
                label = { Text("WhatsApp / Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    focusedLabelColor = GoldPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = guestCountStr,
                    onValueChange = { guestCountStr = it },
                    label = { Text("Guests Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedLabelColor = GoldPrimary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = { totalAmountStr = it },
                    label = { Text("Total Rate (PKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedLabelColor = GoldPrimary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = advanceAmountStr,
                onValueChange = { advanceAmountStr = it },
                label = { Text("Advance Received (PKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    focusedLabelColor = GoldPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Select Shift Slot:", color = TextSecondary, fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slots.forEach { (key, label) ->
                    val selected = slotType == key
                    Surface(
                        onClick = { slotType = key },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) GoldPrimary else DarkSurfaceVariant
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color(0xFF381E72) else TextPrimary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Special Instructions / Amenities Requested") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    focusedLabelColor = GoldPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (guestName.isNotBlank()) {
                        val total = totalAmountStr.toDoubleOrNull() ?: 0.0
                        val advance = advanceAmountStr.toDoubleOrNull() ?: 0.0
                        val cal = Calendar.getInstance()
                        val start = cal.timeInMillis
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                        val end = cal.timeInMillis

                        val newBooking = Booking(
                            startDate = start,
                            endDate = end,
                            slotType = slotType,
                            guestName = guestName,
                            guestPhone = guestPhone,
                            guestCount = guestCountStr.toIntOrNull() ?: 10,
                            totalAmount = total,
                            advanceAmount = advance,
                            remainingAmount = (total - advance).coerceAtLeast(0.0),
                            status = if (advance > 0) "CONFIRMED" else "PENDING_DEPOSIT",
                            bookedByManagerId = activeManager.id,
                            bookedByManagerName = activeManager.name,
                            notes = notes,
                            selectedAmenities = "Swimming Pool, Lawn, Generator Backup"
                        )
                        pendingBooking = newBooking
                        showConfirmationDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF381E72)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("CONFIRM & SAVE BOOKING", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
