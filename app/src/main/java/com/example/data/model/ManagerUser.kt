package com.example.data.model

data class ManagerUser(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val avatarColorHex: Long
) {
    fun getInitials(): String {
        val parts = name.split(" ").filter { it.isNotBlank() }
        return if (parts.size >= 2) {
            "${parts[0].first()}${parts[1].first()}".uppercase()
        } else if (parts.isNotEmpty()) {
            parts[0].take(2).uppercase()
        } else {
            "M"
        }
    }

    companion object {
        val PRESET_MANAGERS = listOf(
            ManagerUser("m1", "Faisal Malik", "Owner / Senior Manager", "+92 300 1234567", 0xFFD4AF37),
            ManagerUser("m2", "Ali Khan", "Booking Admin", "+92 301 2345678", 0xFF10B981),
            ManagerUser("m3", "Usman Ahmed", "Operations Manager", "+92 302 3456789", 0xFF3B82F6),
            ManagerUser("m4", "Hamza Tariq", "Finance Admin", "+92 303 4567890", 0xFF8B5CF6),
            ManagerUser("m5", "Bilal Shah", "Guest Relations", "+92 304 5678901", 0xFFF59E0B)
        )
    }
}
