package com.example.syncid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.data.AccessRecord
import com.example.syncid.ui.theme.AccentBlue
import com.example.syncid.ui.theme.DarkSurface
import com.example.syncid.ui.viewmodel.NfcViewModel
import com.example.syncid.ui.utils.Translations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardUser(
    viewModel: NfcViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToMedicalData: () -> Unit,
    onNavigateToVirtualCard: () -> Unit,
    onNavigateToReportLoss: () -> Unit,
    onNavigateToEmergencyScan: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val currentLang by viewModel.language.collectAsState()
    val t = { key: String -> Translations.getText(key, currentLang) }
    
    val user by viewModel.currentUser.collectAsState()
    val history by viewModel.accessHistory.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TEC-LINK ${t("profile_student")}", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Ya estamos aquí */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = t("home")) },
                    label = { Text(t("home")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.History, contentDescription = t("history")) },
                    label = { Text(t("history")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToEmergencyScan,
                    icon = { Icon(Icons.Default.Nfc, contentDescription = t("emergency")) },
                    label = { Text(t("emergency")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, contentDescription = t("profile")) },
                    label = { Text(t("profile")) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AccentBlue
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user?.name ?: "Carlos Rodríguez", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${t("control_number")}: ${user?.controlNumber ?: "20210458"}", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardItem(t("history"), Icons.Default.History, Modifier.weight(1f), onClick = onNavigateToHistory)
                DashboardItem(t("medical_data"), Icons.Default.MedicalServices, Modifier.weight(1f), onClick = onNavigateToMedicalData)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardItem(t("virtual_card"), Icons.Default.QrCode, Modifier.weight(1f), onClick = onNavigateToVirtualCard)
                DashboardItem(t("report_loss"), Icons.Default.Warning, Modifier.weight(1f), Color.Red, onClick = onNavigateToReportLoss)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de Accesos Recientes
            Text(t("recent_access"), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            history.forEach { record ->
                AccessRecordItem(record)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Sección de Estacionamiento (Debajo del historial)
            Text(t("parking_avail"), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start), color = AccentBlue, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("E-1 Principal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("45 ${t("parking_free")}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.LocalParking, contentDescription = null, tint = AccentBlue)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Matriz de lugares (Estilo similar a la vista de guardia)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(3) { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(8) { col ->
                                    val isOccupied = (row * 8 + col) % 3 == 0
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                if (isOccupied) Color.Red.copy(alpha = 0.6f) 
                                                else Color.Green.copy(alpha = 0.6f),
                                                RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DashboardItem(title: String, icon: ImageVector, modifier: Modifier = Modifier, color: Color = AccentBlue, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Text(title, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun AccessRecordItem(record: AccessRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentBlue)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(record.location, fontWeight = FontWeight.Medium)
                Text(record.timestamp, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
