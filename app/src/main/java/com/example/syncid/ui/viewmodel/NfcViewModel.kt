package com.example.syncid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncid.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

sealed class NfcStatus {
    object Idle : NfcStatus()
    object Scanning : NfcStatus()
    data class Success(val message: String, val student: User? = null) : NfcStatus()
    data class Error(val message: String) : NfcStatus()
}

class NfcViewModel : ViewModel() {
    private val _nfcStatus = MutableStateFlow<NfcStatus>(NfcStatus.Idle)
    val nfcStatus: StateFlow<NfcStatus> = _nfcStatus.asStateFlow()

    private val _language = MutableStateFlow("Español")
    val language = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    // Dashboard Statistics
    private val _studentsInside = MutableStateFlow(1245)
    val studentsInside = _studentsInside.asStateFlow()

    private val _dailyEntries = MutableStateFlow(850)
    val dailyEntries = _dailyEntries.asStateFlow()

    private val _dailyExits = MutableStateFlow(320)
    val dailyExits = _dailyExits.asStateFlow()

    // Alerts
    private val _alerts = MutableStateFlow<List<SystemAlert>>(listOf(
        SystemAlert("1", "SOS Activado", "Alumno: Juan Pérez - Patio Central", AlertLevel.CRITICAL, "12:05 PM", "SOS"),
        SystemAlert("2", "Acceso Denegado", "Intento de entrada con pulsera bloqueada", AlertLevel.WARNING, "11:50 AM", "Unauthorized")
    ))
    val alerts = _alerts.asStateFlow()

    // Parking
    private val _parkingSpots = MutableStateFlow(List(24) { i ->
        ParkingSpot(i, i % 3 != 0, if (i % 3 != 0) "Alumno $i" else null)
    })
    val parkingSpots = _parkingSpots.asStateFlow()

    // Excursion
    private val _excursionStudents = MutableStateFlow(listOf(
        ExcursionStudent("1", "Ana García", true, "Museo de Ciencias"),
        ExcursionStudent("2", "Luis Torres", true, "Museo de Ciencias"),
        ExcursionStudent("3", "Sofía Luna", false, "Desconocido")
    ))
    val excursionStudents = _excursionStudents.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(
        User("1", "Carlos Rodríguez", "20210458", "carlos@institucion.edu.mx", UserRole.GUARDIA)
    )
    val currentUser = _currentUser.asStateFlow()

    private val _medicalData = MutableStateFlow(MedicalData("O+", "Ninguna", false, "555-0199"))
    val medicalData = _medicalData.asStateFlow()

    private val _accessHistory = MutableStateFlow(listOf(
        AccessRecord("1", "Carlos Rodríguez", "Puerta Principal Sur", "12:10 PM", "Entrada", "Permitido"),
        AccessRecord("2", "Ana García", "Estacionamiento", "11:45 AM", "Entrada", "Permitido"),
        AccessRecord("3", "Desconocido", "Puerta Norte", "11:30 AM", "Error", "Denegado")
    ))
    val accessHistory = _accessHistory.asStateFlow()

    private fun getCurrentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun simulateNfcScan() {
        viewModelScope.launch {
            _nfcStatus.value = NfcStatus.Scanning
            delay(1500)
            val success = Random.nextBoolean()
            if (success) {
                val mockStudent = User("st1", "Roberto Gómez", "2023001", "roberto@edu.mx", UserRole.ALUMNO, group = "6to A")
                _nfcStatus.value = NfcStatus.Success("Acceso Permitido", mockStudent)
                
                val newRecord = AccessRecord(
                    UUID.randomUUID().toString(),
                    mockStudent.name,
                    "Puerta Principal",
                    getCurrentTime(),
                    "Entrada",
                    "Permitido"
                )
                _accessHistory.value = listOf(newRecord) + _accessHistory.value
                _studentsInside.value += 1
                _dailyEntries.value += 1
            } else {
                _nfcStatus.value = NfcStatus.Error("Acceso Denegado: Pulsera no válida")
                val newRecord = AccessRecord(
                    UUID.randomUUID().toString(),
                    "Desconocido",
                    "Puerta Principal",
                    getCurrentTime(),
                    "Error",
                    "Denegado"
                )
                _accessHistory.value = listOf(newRecord) + _accessHistory.value
                
                addAlert("Acceso No Autorizado", "Se detectó un intento de acceso con credencial inválida", AlertLevel.WARNING, "Unauthorized")
            }
            delay(4000)
            _nfcStatus.value = NfcStatus.Idle
        }
    }

    fun addAlert(title: String, message: String, level: AlertLevel, type: String) {
        val id = UUID.randomUUID().toString()
        val newAlert = SystemAlert(
            id,
            title,
            message,
            level,
            getCurrentTime(),
            type
        )
        _alerts.value = listOf(newAlert) + _alerts.value

        // Si es una alerta SOS o Crítica, programar su auto-eliminación después de 10 segundos
        if (level == AlertLevel.CRITICAL || type == "SOS") {
            viewModelScope.launch {
                delay(10000) // 10 segundos
                removeAlert(id)
            }
        }
    }

    fun removeAlert(id: String) {
        _alerts.value = _alerts.value.filter { it.id != id }
    }

    fun toggleExcursionStudent(id: String) {
        _excursionStudents.value = _excursionStudents.value.map {
            if (it.id == id) it.copy(isPresent = !it.isPresent) else it
        }
    }

    fun toggleParkingSpot(id: Int) {
        _parkingSpots.value = _parkingSpots.value.map {
            if (it.id == id) it.copy(isOccupied = !it.isOccupied) else it
        }
    }

    fun updateUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value?.copy(role = role)
    }

    fun setLanguage(newLanguage: String) { _language.value = newLanguage }
    fun toggleTheme(isDark: Boolean) { _isDarkTheme.value = isDark }

    fun resetSettings() {
        _language.value = "Español"
        _isDarkTheme.value = true
    }
}
