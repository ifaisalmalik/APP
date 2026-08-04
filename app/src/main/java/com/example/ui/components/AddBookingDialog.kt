package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.ManagerUser
import com.example.ui.theme.AmberPending
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookingSheet(
    activeManager: ManagerUser,
    collisionWarnings: List<Booking>,
    onCheckCollisions: (Long, Long) -> Unit,
    onSaveBooking: (
        startDate: Long,
        endDate: Long,
        slotType: String,
        guestName: String,
        guestPhone: String,
        guestCount: Int,
        totalAmount: Double,
        advanceAmount: Double,
        status: String,
        notes: String,
        amenities: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val calendar = Calendar.getInstance()
    var startDateMs by remember { mutableLongStateOf(calendar.timeInMillis) }
    
    // Default 1 day
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    var endDateMs by remember { mutableLongStateOf(calendar.timeInMillis) }

    var guestName by remember { mutableStateOf("") }
    var guestPhone by remember { mutableStateOf("") }
    var guestCountStr by remember { mutableStateOf("15") }
    var totalAmountStr by remember { mutableStateOf("85000") }
    var advanceAmountStr by remember { mutableStateOf("35000") }
    var slotType by remember { mutableStateOf("FULL_DAY") }
    var status by remember { mutableStateOf("CONFIRMED") }
    var notes by remember { mutableStateOf("") }

    val defaultAmenities = listOf(
        "Swimming Pool", "Lawn", "Generator Backup", "BBQ Grill", "Sound System", "Luxury Bedrooms"
    )
    val selectedAmenities = remember { mutableStateListOf<String>().apply { addAll(defaultAmenities.take(3)) } }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }

    // Trigger collision check when dates change
    LaunchedEffect(startDateMs, endDateMs) {
        onCheckCollisions(startDateMs, endDateMs)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = GoldPrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "New Farmhouse Reservation",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Operating Manager: ${activeManager.name}",
                            color = GoldPrimary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Form Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Collision Warning Banner
                if (collisionWarnings.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CoralRed.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CoralRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "⚠️ DATE CONFLICT DETECTED!",
                                    color = CoralRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                collisionWarnings.forEach { conflict ->
                                    Text(
                                        text = "• Already booked by ${conflict.bookedByManagerName} for '${conflict.guestName}' (${conflict.formatDateRange()})",
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Guest Details Card
                Text(
                    text = "GUEST INFORMATION",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = guestName,
                    onValueChange = {
                        guestName = it
                        nameError = false
                    },
                    label = { Text("Guest Name (e.g., Chaudhry Kamran)") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = GoldPrimary) },
                    isError = nameError,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = guestPhone,
                        onValueChange = {
                            guestPhone = it
                            phoneError = false
                        },
                        label = { Text("Phone / WhatsApp") },
                        leadingIcon = { Icon(Icons.Default.Call, null, tint = GoldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError,
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = guestCountStr,
                        onValueChange = { guestCountStr = it },
                        label = { Text("Guest Count") },
                        leadingIcon = { Icon(Icons.Default.Groups, null, tint = GoldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Selection Section
                Text(
                    text = "RESERVATION DATES & TIME SLOT",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Start Date Button
                    Surface(
                        onClick = {
                            val c = Calendar.getInstance().apply { timeInMillis = startDateMs }
                            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                                val selCal = Calendar.getInstance()
                                selCal.set(year, month, dayOfMonth, 0, 0, 0)
                                startDateMs = selCal.timeInMillis
                                if (endDateMs < startDateMs) {
                                    endDateMs = startDateMs
                                }
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Start Date", color = TextMuted, fontSize = 10.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(dateFormat.format(Date(startDateMs)), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    // End Date Button
                    Surface(
                        onClick = {
                            val c = Calendar.getInstance().apply { timeInMillis = endDateMs }
                            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                                val selCal = Calendar.getInstance()
                                selCal.set(year, month, dayOfMonth, 23, 59, 59)
                                endDateMs = selCal.timeInMillis
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("End Date", color = TextMuted, fontSize = 10.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(dateFormat.format(Date(endDateMs)), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Time Slot Choice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SlotTypeChip("Full Day (24h)", selected = slotType == "FULL_DAY") { slotType = "FULL_DAY" }
                    SlotTypeChip("Day (9am-6pm)", selected = slotType == "DAY_SHIFT") { slotType = "DAY_SHIFT" }
                    SlotTypeChip("Night (8pm-7am)", selected = slotType == "NIGHT_SHIFT") { slotType = "NIGHT_SHIFT" }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Details
                Text(
                    text = "PRICING & ADVANCE DEPOSIT (PKR)",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = totalAmountStr,
                        onValueChange = { totalAmountStr = it },
                        label = { Text("Total Rent (PKR)") },
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, null, tint = GoldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = advanceAmountStr,
                        onValueChange = { advanceAmountStr = it },
                        label = { Text("Advance Paid (PKR)") },
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, null, tint = EmeraldGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Booking Status Choice
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Status: ", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { status = "CONFIRMED" }
                    ) {
                        RadioButton(
                            selected = status == "CONFIRMED",
                            onClick = { status = "CONFIRMED" },
                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldGreen)
                        )
                        Text("Confirmed", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { status = "PENDING_DEPOSIT" }
                    ) {
                        RadioButton(
                            selected = status == "PENDING_DEPOSIT",
                            onClick = { status = "PENDING_DEPOSIT" },
                            colors = RadioButtonDefaults.colors(selectedColor = AmberPending)
                        )
                        Text("Pending Advance", color = AmberPending, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Facilities Checkboxes
                Text(
                    text = "AMENITIES REQUESTED",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    defaultAmenities.chunked(2).forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            pair.forEach { amenity ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (selectedAmenities.contains(amenity)) selectedAmenities.remove(
                                                amenity
                                            )
                                            else selectedAmenities.add(amenity)
                                        }
                                ) {
                                    Checkbox(
                                        checked = selectedAmenities.contains(amenity),
                                        onCheckedChange = {
                                            if (it) selectedAmenities.add(amenity) else selectedAmenities.remove(
                                                amenity
                                            )
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = GoldPrimary,
                                            checkmarkColor = Color.Black
                                        )
                                    )
                                    Text(text = amenity, color = TextPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Requests / Catering / Notes") },
                    leadingIcon = { Icon(Icons.Default.NoteAdd, null, tint = GoldPrimary) },
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Save Action Button
            Button(
                onClick = {
                    if (guestName.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    if (guestPhone.isBlank()) {
                        phoneError = true
                        return@Button
                    }

                    val totalAmt = totalAmountStr.toDoubleOrNull() ?: 0.0
                    val advanceAmt = advanceAmountStr.toDoubleOrNull() ?: 0.0
                    val guestCnt = guestCountStr.toIntOrNull() ?: 10

                    onSaveBooking(
                        startDateMs,
                        endDateMs,
                        slotType,
                        guestName,
                        guestPhone,
                        guestCnt,
                        totalAmt,
                        advanceAmt,
                        status,
                        notes,
                        selectedAmenities.joinToString(", ")
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (collisionWarnings.isNotEmpty()) "Save Booking (Override Collision)" else "Confirm & Notify Team",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SlotTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) GoldPrimary else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            color = if (selected) Color.Black else TextPrimary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = GoldPrimary.copy(alpha = 0.3f),
    focusedLabelColor = GoldPrimary,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = GoldPrimary
)
