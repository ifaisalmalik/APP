package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CalendarDay(
    val dayOfMonth: Int,
    val timestamp: Long,
    val isCurrentMonth: Boolean,
    val booking: Booking? = null
)

@Composable
fun AvailabilityCalendarView(
    bookings: List<Booking>,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDayInfo by remember { mutableStateOf<CalendarDay?>(null) }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    // Generate days for grid
    val daysInMonth = remember(calendar.timeInMillis, bookings) {
        getDaysForCalendarMonth(calendar, bookings)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Month Header with Prev/Next controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newCal = calendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, -1)
                        calendar = newCal
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = monthYearFormat.format(calendar.time),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                IconButton(
                    onClick = {
                        val newCal = calendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, 1)
                        calendar = newCal
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Day of week labels
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                daysOfWeek.forEach { dayLabel ->
                    Text(
                        text = dayLabel,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                items(daysInMonth) { calDay ->
                    val isBooked = calDay.booking != null && calDay.booking.status == "CONFIRMED"
                    val isPending = calDay.booking != null && calDay.booking.status == "PENDING_DEPOSIT"

                    val bg = when {
                        !calDay.isCurrentMonth -> DarkSurfaceVariant.copy(alpha = 0.3f)
                        isBooked -> CoralRed.copy(alpha = 0.25f)
                        isPending -> AmberPending.copy(alpha = 0.25f)
                        else -> EmeraldGreen.copy(alpha = 0.15f)
                    }

                    val borderColor = when {
                        !calDay.isCurrentMonth -> Color.Transparent
                        isBooked -> CoralRed
                        isPending -> AmberPending
                        else -> EmeraldGreen.copy(alpha = 0.5f)
                    }

                    val textColor = when {
                        !calDay.isCurrentMonth -> TextMuted
                        isBooked -> CoralRed
                        isPending -> AmberPending
                        else -> TextPrimary
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(bg)
                            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                            .clickable(enabled = calDay.isCurrentMonth) {
                                selectedDayInfo = calDay
                                onDayClick(calDay)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = calDay.dayOfMonth.toString(),
                                color = textColor,
                                fontWeight = if (isBooked || isPending) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                            if (isBooked || isPending) {
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(borderColor)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendBadge(color = EmeraldGreen, label = "Available")
                LegendBadge(color = CoralRed, label = "Booked")
                LegendBadge(color = AmberPending, label = "Pending Hold")
            }

            // Selected Day Info Banner
            selectedDayInfo?.let { dayInfo ->
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
                            Text(
                                text = sdf.format(dayInfo.timestamp),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (dayInfo.booking != null) {
                                Text(
                                    text = "BOOKED: ${dayInfo.booking.guestName} (${dayInfo.booking.getSlotLabel()})",
                                    color = CoralRed,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Booked by Manager: ${dayInfo.booking.bookedByManagerName}",
                                    color = GoldAccent,
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = "AVAILABLE for Booking! Tap '+' button below to reserve.",
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendBadge(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

private fun getDaysForCalendarMonth(calendar: Calendar, bookings: List<Booking>): List<CalendarDay> {
    val tempCal = calendar.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH, 1)
    
    val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1 // 0-based index
    val maxDaysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val daysList = mutableListOf<CalendarDay>()

    // Fill padding days from previous month
    val prevMonthCal = tempCal.clone() as Calendar
    prevMonthCal.add(Calendar.MONTH, -1)
    val maxDaysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    for (i in (maxDaysInPrevMonth - firstDayOfWeek + 1)..maxDaysInPrevMonth) {
        prevMonthCal.set(Calendar.DAY_OF_MONTH, i)
        daysList.add(CalendarDay(i, prevMonthCal.timeInMillis, false, null))
    }

    // Fill days of current month
    for (i in 1..maxDaysInMonth) {
        tempCal.set(Calendar.DAY_OF_MONTH, i)
        val dayStartMs = tempCal.timeInMillis
        tempCal.set(Calendar.HOUR_OF_DAY, 23)
        tempCal.set(Calendar.MINUTE, 59)
        val dayEndMs = tempCal.timeInMillis
        tempCal.set(Calendar.HOUR_OF_DAY, 0)
        tempCal.set(Calendar.MINUTE, 0)

        // Find matching active booking
        val matchedBooking = bookings.firstOrNull { booking ->
            booking.status != "CANCELLED" &&
                    booking.startDate <= dayEndMs &&
                    booking.endDate >= dayStartMs
        }

        daysList.add(CalendarDay(i, dayStartMs, true, matchedBooking))
    }

    // Fill remaining trailing days to make complete weeks
    var trailingDay = 1
    val nextMonthCal = tempCal.clone() as Calendar
    nextMonthCal.add(Calendar.MONTH, 1)

    while (daysList.size % 7 != 0 || daysList.size < 35) {
        nextMonthCal.set(Calendar.DAY_OF_MONTH, trailingDay)
        daysList.add(CalendarDay(trailingDay, nextMonthCal.timeInMillis, false, null))
        trailingDay++
    }

    return daysList
}
