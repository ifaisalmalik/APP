package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookingCard(
    booking: Booking,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    val statusColor = when (booking.status) {
        "CONFIRMED" -> EmeraldGreen
        "PENDING_DEPOSIT" -> AmberPending
        "CANCELLED" -> CoralRed
        else -> TextSecondary
    }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
            maximumFractionDigits = 0
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Guest Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = booking.guestName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = booking.guestPhone,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.clickable {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.guestPhone}"))
                                    context.startActivity(dialIntent)
                                }
                            )
                        }
                    }
                }

                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = booking.status.replace("_", " "),
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextMuted
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(DarkSurfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mark Confirmed", color = EmeraldGreen) },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = EmeraldGreen) },
                            onClick = {
                                onStatusChange("CONFIRMED")
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mark Pending Deposit", color = AmberPending) },
                            leadingIcon = { Icon(Icons.Default.Schedule, null, tint = AmberPending) },
                            onClick = {
                                onStatusChange("PENDING_DEPOSIT")
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cancel Booking", color = CoralRed) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = CoralRed) },
                            onClick = {
                                onStatusChange("CANCELLED")
                                menuExpanded = false
                            }
                        )
                        HorizontalDivider(color = DarkSurface)
                        DropdownMenuItem(
                            text = { Text("Delete Permanently", color = CoralRed) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = CoralRed) },
                            onClick = {
                                onDelete()
                                menuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date & Slot details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = booking.formatDateRange(),
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${booking.guestCount} Guests",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Slot: ${booking.getSlotLabel()}",
                color = GoldAccent,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Price", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = "PKR ${booking.totalAmount.toInt()}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Column {
                    Text("Advance Paid", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = "PKR ${booking.advanceAmount.toInt()}",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Column {
                    Text("Remaining Due", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = "PKR ${booking.remainingAmount.toInt()}",
                        color = if (booking.remainingAmount > 0) CoralRed else EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            if (booking.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notes: ${booking.notes}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer Row: Booked By Manager Tag & WhatsApp Share Receipt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booked by: ${booking.bookedByManagerName}",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )

                // Share Receipt on WhatsApp / Message Button
                Surface(
                    onClick = { shareWhatsAppReceipt(context, booking) },
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Share Receipt",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

private fun shareWhatsAppReceipt(context: Context, booking: Booking) {
    val receiptText = """
        🏰 *THE LORD'S FARM HOUSE*
        ------------------------------
        *BOOKING CONFIRMATION RECEIPT*
        ------------------------------
        👤 *Guest Name:* ${booking.guestName}
        📞 *Contact:* ${booking.guestPhone}
        📅 *Dates:* ${booking.formatDateRange()}
        ⏰ *Slot:* ${booking.getSlotLabel()}
        👥 *Guests:* ${booking.guestCount} Persons
        ------------------------------
        💰 *Total Amount:* PKR ${booking.totalAmount.toInt()}
        💵 *Advance Paid:* PKR ${booking.advanceAmount.toInt()}
        🔴 *Remaining Balance:* PKR ${booking.remainingAmount.toInt()}
        📌 *Status:* ${booking.status}
        👨‍💼 *Reserved By Manager:* ${booking.bookedByManagerName}
        ------------------------------
        ✨ *Amenities:* ${booking.selectedAmenities}
        
        Thank you for choosing *The Lord's Farm House*!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, receiptText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Booking Receipt"))
}
