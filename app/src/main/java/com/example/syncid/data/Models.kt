package com.example.syncid.data

enum class UserRole {
    USUARIO, GUARDIA, ADMINISTRADOR, MAESTRO, ALUMNO
}

data class User(
    val id: String,
    val name: String,
    val controlNumber: String,
    val email: String,
    val role: UserRole,
    val isBraceletBlocked: Boolean = false,
    val group: String = "S/G"
)

data class MedicalData(
    val bloodType: String = "",
    val allergies: String = "",
    val asthma: Boolean = false,
    val emergencyContact: String = ""
)

data class AccessRecord(
    val id: String,
    val userName: String,
    val location: String,
    val timestamp: String,
    val type: String, // e.g., "Entrada", "Salida", "Error"
    val status: String = "Permitido" // "Permitido", "Denegado"
)

enum class AlertLevel {
    INFO, WARNING, CRITICAL
}

data class SystemAlert(
    val id: String,
    val title: String,
    val message: String,
    val level: AlertLevel,
    val timestamp: String,
    val type: String // "Unauthorized", "OutOfHours", "SOS"
)

data class ParkingSpot(
    val id: Int,
    val isOccupied: Boolean,
    val studentName: String? = null
)

data class ExcursionStudent(
    val id: String,
    val name: String,
    val isPresent: Boolean,
    val lastKnownLocation: String? = null
)
