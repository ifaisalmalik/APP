package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val managerName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val bookingId: Long? = null,
    val isRead: Boolean = false,
    val alertType: String = "BOOKING_CREATED" // "BOOKING_CREATED", "BOOKING_CANCELLED", "PAYMENT_RECEIVED", "SYSTEM"
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
