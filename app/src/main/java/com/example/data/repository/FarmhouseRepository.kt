package com.example.data.repository

import com.example.data.dao.BookingDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.NotificationDao
import com.example.data.model.Booking
import com.example.data.model.FarmhouseExpense
import com.example.data.model.NotificationItem
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FarmhouseRepository(
    private val bookingDao: BookingDao,
    private val notificationDao: NotificationDao,
    private val expenseDao: ExpenseDao
) {
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()
    val activeBookings: Flow<List<Booking>> = bookingDao.getActiveBookings()
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = notificationDao.getUnreadCount()
    val allExpenses: Flow<List<FarmhouseExpense>> = expenseDao.getAllExpenses()

    suspend fun checkOverlappingBookings(startDate: Long, endDate: Long): List<Booking> {
        return bookingDao.getOverlappingBookings(startDate, endDate)
    }

    suspend fun createBooking(booking: Booking, managerName: String): Long {
        val bookingId = bookingDao.insertBooking(booking)
        
        // Log Notification for Team
        val notif = NotificationItem(
            title = "New Booking Added",
            message = "${booking.guestName} booked for ${booking.formatDateRange()} (${booking.getSlotLabel()}) by $managerName",
            managerName = managerName,
            bookingId = bookingId,
            alertType = "BOOKING_CREATED"
        )
        notificationDao.insertNotification(notif)
        return bookingId
    }

    suspend fun updateBookingStatus(booking: Booking, newStatus: String, managerName: String) {
        val updated = booking.copy(status = newStatus)
        bookingDao.updateBooking(updated)

        val notif = NotificationItem(
            title = "Booking Status Updated",
            message = "Booking for ${booking.guestName} status changed to $newStatus by $managerName",
            managerName = managerName,
            bookingId = booking.id,
            alertType = if (newStatus == "CANCELLED") "BOOKING_CANCELLED" else "BOOKING_UPDATED"
        )
        notificationDao.insertNotification(notif)
    }

    suspend fun deleteBooking(booking: Booking, managerName: String) {
        bookingDao.deleteBooking(booking)
        val notif = NotificationItem(
            title = "Booking Deleted",
            message = "Booking for ${booking.guestName} (${booking.formatDateRange()}) removed by $managerName",
            managerName = managerName,
            alertType = "BOOKING_CANCELLED"
        )
        notificationDao.insertNotification(notif)
    }

    suspend fun addExpense(expense: FarmhouseExpense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: FarmhouseExpense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun markNotificationsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun clearNotifications() {
        notificationDao.clearAllNotifications()
    }

    suspend fun seedInitialDataIfEmpty() {
        // App starts completely clean and fresh without pre-populated mock data
    }
}
