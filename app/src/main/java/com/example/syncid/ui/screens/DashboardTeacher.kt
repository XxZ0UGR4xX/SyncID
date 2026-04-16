package com.example.syncid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.AccentBlue
import com.example.syncid.ui.theme.DarkSurface
import com.example.syncid.ui.viewmodel.NfcViewModel
import com.example.syncid.ui.utils.Translations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTeacher(
    viewModel: NfcViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToEmergencyScan: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val currentLang by viewModel.language.collectAsState()
    val t = { key: String -> Translations.getText(key, currentLang) }
    val user by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("SyncID", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AccentBlue)
                        Text(t("teacher"), fontSize = 12.sp, color = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Home */ },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(t("home")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text(t("history")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, null) },
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
                .padding(20.dp)
        ) {
            // Header con bienvenida
            Text(
                "${t("welcome")},",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                user?.name ?: "Profesor",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card Principal: Estado de Clase
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(t("students_in_class"), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text("24 / 30", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                        }
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White.copy(alpha = 0.3f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { 24/30f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                        color = Color.White,
                        trackColor = Color.Transparent
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Herramientas del Maestro
            Text(t("teacher_tools"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TeacherToolItem(
                    title = t("take_attendance"),
                    icon = Icons.Default.QrCodeScanner,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToScanner
                )
                TeacherToolItem(
                    title = t("medical_consultation"),
                    icon = Icons.Default.HealthAndSafety,
                    color = Color(0xFF00C853),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToEmergencyScan
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Lista de alumnos recientes
            Text(t("scanned_students"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            val recentStudents = listOf(
                "Carlos Mendoza" to "07:55 AM",
                "Ana Lucía Silva" to "07:58 AM",
                "Roberto Gómez" to "08:02 AM"
            )

            recentStudents.forEach { (name, time) ->
                StudentRow(name, time)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TeacherToolItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun StudentRow(name: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(32.dp).background(AccentBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp), tint = AccentBlue)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
