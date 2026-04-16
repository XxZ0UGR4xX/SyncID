package com.example.syncid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncid.data.AccessRecord
import com.example.syncid.data.MedicalData
import com.example.syncid.data.User
import com.example.syncid.data.UserRole
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
    data class Success(val message: String) : NfcStatus()
    data class Error(val message: String) : NfcStatus()
}

class NfcViewModel : ViewModel() {
    private val _nfcStatus = MutableStateFlow<NfcStatus>(NfcStatus.Idle)
    val nfcStatus: StateFlow<NfcStatus> = _nfcStatus.asStateFlow()

    private val _language = MutableStateFlow("Español")
    val language = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    fun setLanguage(newLanguage: String) {
        _language.value = newLanguage
    }

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun resetSettings() {
        _language.value = "Español"
        _isDarkTheme.value = true
    }

    private val _currentUser = MutableStateFlow<User?>(
        User("1", "Carlos Rodríguez", "20210458", "carlos@institucion.edu.mx", UserRole.USUARIO)
    )
    val currentUser = _currentUser.asStateFlow()

    private val _medicalData = MutableStateFlow(MedicalData("O+", "Ninguna", false, "555-0199"))
    val medicalData = _medicalData.asStateFlow()

    private val _qrCodeId = MutableStateFlow(Random.nextInt())
    val qrCodeId = _qrCodeId.asStateFlow()

    private val _accessHistory = MutableStateFlow(listOf(
        AccessRecord("1", "Carlos Rodríguez", "Puerta Principal Sur", getCurrentTime(), "Entrada"),
        AccessRecord("2", "Carlos Rodríguez", "Biblioteca Central", "10:30 AM", "Entrada")
    ))
    val accessHistory = _accessHistory.asStateFlow()

    private fun getCurrentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun refreshQrCode() {
        _qrCodeId.value = Random.nextInt()
    }

    fun updateMedicalData(newData: MedicalData) {
        _medicalData.value = newData
    }

    fun simulateNfcScan(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _nfcStatus.value = NfcStatus.Scanning
            delay(2000)
            val success = (1..10).random() > 2
            if (success) {
                _nfcStatus.value = NfcStatus.Success("Acceso Permitido")
            } else {
                _nfcStatus.value = NfcStatus.Error("Acceso Denegado")
            }
            onResult(success)
            delay(3000)
            _nfcStatus.value = NfcStatus.Idle
        }
    }

    fun reportLostBracelet() {
        _currentUser.value = _currentUser.value?.copy(isBraceletBlocked = true)
    }

    fun updateUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value?.copy(role = role)
    }
}
