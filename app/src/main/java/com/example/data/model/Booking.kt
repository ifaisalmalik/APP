package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: Long, // Epoch ms for 00:00:00 of start day
    val endDate: Long,   // Epoch ms for 23:59:59 of end day
    val slotType: String = "FULL_DAY", // "FULL_DAY", "DAY_SHIFT", "NIGHT_SHIFT"
    val guestName: String,
    val guestPhone: String,
    val guestCount: Int = 10,
    val totalAmount: Double,
    val advanceAmount: Double,
    val remainingAmount: Double = totalAmount - advanceAmount,
    val status: String = "CONFIRMED", // "CONFIRMED", "PENDING_DEPOSIT", "CANCELLED"
    val bookedByManagerId: String,
    val bookedByManagerName: String,
    val notes: String = "",
    val selectedAmenities: String = "Swimming Pool, Lawn, Generator Backup",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formatDateRange(): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val startStr = sdf.format(Date(startDate))
        val endStr = sdf.format(Date(endDate))
        return if (startStr == endStr) startStr else "$startStr - $endStr"
    }

    fun getSlotLabel(): String {
        return when (slotType) {
            "DAY_SHIFT" -> "Day Shift (9 AM - 6 PM)"
            "NIGHT_SHIFT" -> "Night Shift (8 PM - 7 AM)"
            else -> "Full Day / 24 Hours"
        }
    }
}
