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
    val isBraceletBlocked: Boolean = false
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
    val type: String // e.g., "Entrada", "Salida"
)
