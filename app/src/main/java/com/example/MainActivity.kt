package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagerUser
import com.example.ui.components.NewBookingSheet
import com.example.ui.components.NotificationsSheet
import com.example.ui.screens.BookingsScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.FinancialsScreen
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorderColor
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LordsFarmhouseTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.FarmhouseViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FarmhouseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LordsFarmhouseTheme {
                FarmhouseAppMain(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FarmhouseAppMain(viewModel: FarmhouseViewModel) {
    val bookings by viewModel.allBookings.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val activeManager by viewModel.activeManager.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showNewBookingSheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    val activeBookingsCount = bookings.count { it.status != "CANCELLED" }
    val unreadNotifsCount = notifications.count { !it.isRead }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBarElegant(
                activeManager = activeManager,
                unreadCount = unreadNotifsCount,
                onOpenNotifications = { showNotificationsSheet = true }
            )
        },
        bottomBar = {
            BottomNavBarElegant(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewBookingSheet = true },
                containerColor = GoldPrimary,
                contentColor = Color(0xFF381E72),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Booking", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NEW BOOKING", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Stats Banner Header
            QuickStatsHeader(activeBookings = activeBookingsCount)

            // Dynamic Screen Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> CalendarScreen(
                        bookings = bookings,
                        onStatusChange = { booking, status -> viewModel.updateBookingStatus(booking, status) },
                        onDeleteBooking = { viewModel.deleteBooking(it) },
                        onCreateBookingClick = { showNewBookingSheet = true }
                    )
                    1 -> BookingsScreen(
                        bookings = bookings,
                        searchQuery = searchQuery,
                        statusFilter = statusFilter,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onStatusFilterChange = { viewModel.setStatusFilter(it) },
                        onStatusChange = { booking, status -> viewModel.updateBookingStatus(booking, status) },
                        onDeleteBooking = { viewModel.deleteBooking(it) },
                        onCreateBookingClick = { showNewBookingSheet = true }
                    )
                    2 -> FinancialsScreen(
                        bookings = bookings,
                        expenses = expenses,
                        activeManager = activeManager,
                        onAddExpense = { title, cat, amt, notes ->
                            viewModel.addExpense(title, cat, amt, notes, onSuccess = {})
                        },
                        onDeleteExpense = { viewModel.deleteExpense(it) }
                    )
                    3 -> CalendarScreen(
                        bookings = bookings,
                        onStatusChange = { booking, status -> viewModel.updateBookingStatus(booking, status) },
                        onDeleteBooking = { viewModel.deleteBooking(it) },
                        onCreateBookingClick = { showNewBookingSheet = true }
                    )
                }
            }
        }
    }

    if (showNewBookingSheet) {
        NewBookingSheet(
            activeManager = activeManager,
            onSaveBooking = { newBooking ->
                viewModel.addBooking(
                    startDate = newBooking.startDate,
                    endDate = newBooking.endDate,
                    slotType = newBooking.slotType,
                    guestName = newBooking.guestName,
                    guestPhone = newBooking.guestPhone,
                    guestCount = newBooking.guestCount,
                    totalAmount = newBooking.totalAmount,
                    advanceAmount = newBooking.advanceAmount,
                    status = newBooking.status,
                    notes = newBooking.notes,
                    amenities = newBooking.selectedAmenities,
                    onSuccess = { showNewBookingSheet = false }
                )
            },
            onDismiss = { showNewBookingSheet = false }
        )
    }

    if (showNotificationsSheet) {
        NotificationsSheet(
            notifications = notifications,
            managers = ManagerUser.PRESET_MANAGERS,
            activeManager = activeManager,
            onSwitchManager = { mgr ->
                viewModel.setActiveManager(mgr)
            },
            onClearAll = { viewModel.clearAllNotifications() },
            onDismiss = { showNotificationsSheet = false }
        )
    }
}

@Composable
private fun TopAppBarElegant(
    activeManager: ManagerUser,
    unreadCount: Int,
    onOpenNotifications: () -> Unit
) {
    Surface(
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Farmhouse Pro",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Online • ${activeManager.name}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notification Bell with badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CoralRed)
                                .align(Alignment.TopEnd)
                        )
                    }
                }

                // Manager Profile Avatar Initials
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GoldDark)
                        .border(1.dp, GoldPrimary, CircleShape)
                        .clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeManager.getInitials(),
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsHeader(activeBookings: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderColor),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text("THIS MONTH", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Text("$activeBookings Booked", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderColor),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text("AVAILABLE", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Text("${30 - activeBookings} Days", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BottomNavBarElegant(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary,
        tonalElevation = 4.dp,
        modifier = Modifier
            .height(58.dp)
            .border(0.5.dp, DarkBorderColor, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        val items = listOf(
            Triple(0, "Schedule", Icons.Default.CalendarMonth),
            Triple(1, "Bookings", Icons.Default.DateRange),
            Triple(2, "Financials", Icons.Default.MonetizationOn),
            Triple(3, "Team", Icons.Default.Group)
        )

        items.forEach { (index, label, icon) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) },
                label = { Text(label, fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF381E72),
                    selectedTextColor = GoldPrimary,
                    indicatorColor = GoldPrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
