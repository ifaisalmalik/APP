package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Booking
import com.example.data.model.FarmhouseExpense
import com.example.data.model.ManagerUser
import com.example.data.model.NotificationItem
import com.example.data.repository.FarmhouseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FarmhouseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FarmhouseRepository

    // Current active manager profile (Default to Faisal Malik)
    private val _activeManager = MutableStateFlow(ManagerUser.PRESET_MANAGERS[0])
    val activeManager: StateFlow<ManagerUser> = _activeManager.asStateFlow()

    // Search query for bookings
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Status filter: "ALL", "CONFIRMED", "PENDING_DEPOSIT", "CANCELLED"
    private val _statusFilter = MutableStateFlow("ALL")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    // Conflict check state when creating a new booking
    private val _dateCollisionWarnings = MutableStateFlow<List<Booking>>(emptyList())
    val dateCollisionWarnings: StateFlow<List<Booking>> = _dateCollisionWarnings.asStateFlow()

    // System Alert Banner state (e.g. "Booking saved successfully & team notified!")
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FarmhouseRepository(db.bookingDao(), db.notificationDao(), db.expenseDao())

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val allBookings: StateFlow<List<Booking>> = combine(
        repository.allBookings,
        _searchQuery,
        _statusFilter
    ) { bookings, query, status ->
        bookings.filter { booking ->
            val matchesQuery = query.isBlank() ||
                    booking.guestName.contains(query, ignoreCase = true) ||
                    booking.guestPhone.contains(query, ignoreCase = true) ||
                    booking.bookedByManagerName.contains(query, ignoreCase = true) ||
                    booking.notes.contains(query, ignoreCase = true)

            val matchesStatus = status == "ALL" || booking.status == status
            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allExpenses: StateFlow<List<FarmhouseExpense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setActiveManager(manager: ManagerUser) {
        _activeManager.value = manager
        _userMessage.value = "Switched active manager to ${manager.name}"
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: String) {
        _statusFilter.value = filter
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun checkDateCollisions(startDate: Long, endDate: Long, excludeBookingId: Long? = null) {
        viewModelScope.launch {
            val conflicts = repository.checkOverlappingBookings(startDate, endDate)
                .filter { it.id != excludeBookingId }
            _dateCollisionWarnings.value = conflicts
        }
    }

    fun clearCollisionWarnings() {
        _dateCollisionWarnings.value = emptyList()
    }

    fun addBooking(
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
        amenities: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentManager = _activeManager.value
            val booking = Booking(
                startDate = startDate,
                endDate = endDate,
                slotType = slotType,
                guestName = guestName,
                guestPhone = guestPhone,
                guestCount = guestCount,
                totalAmount = totalAmount,
                advanceAmount = advanceAmount,
                remainingAmount = (totalAmount - advanceAmount).coerceAtLeast(0.0),
                status = status,
                bookedByManagerId = currentManager.id,
                bookedByManagerName = currentManager.name,
                notes = notes,
                selectedAmenities = amenities
            )

            repository.createBooking(booking, currentManager.name)
            _userMessage.value = "Booking added for $guestName! Team notified."
            onSuccess()
        }
    }

    fun updateBookingStatus(booking: Booking, newStatus: String) {
        viewModelScope.launch {
            val currentManager = _activeManager.value
            repository.updateBookingStatus(booking, newStatus, currentManager.name)
            _userMessage.value = "Booking status updated to $newStatus"
        }
    }

    fun deleteBooking(booking: Booking) {
        viewModelScope.launch {
            val currentManager = _activeManager.value
            repository.deleteBooking(booking, currentManager.name)
            _userMessage.value = "Booking deleted and team notified."
        }
    }

    fun addExpense(title: String, category: String, amount: Double, notes: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentManager = _activeManager.value
            val expense = FarmhouseExpense(
                title = title,
                category = category,
                amount = amount,
                loggedByManagerName = currentManager.name,
                notes = notes
            )
            repository.addExpense(expense)
            _userMessage.value = "Expense PKR ${amount.toInt()} logged."
            onSuccess()
        }
    }

    fun deleteExpense(expense: FarmhouseExpense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _userMessage.value = "Expense item deleted."
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
            _userMessage.value = "Notification center cleared."
        }
    }
}
