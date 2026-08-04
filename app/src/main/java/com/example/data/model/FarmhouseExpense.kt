package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "expenses")
data class FarmhouseExpense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Pool Maintenance", "Generator Fuel", "Staff Salary", "Repairs", "Utilities"
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val loggedByManagerName: String,
    val notes: String = ""
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }
}
