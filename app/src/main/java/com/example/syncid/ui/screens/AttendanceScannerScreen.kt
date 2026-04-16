package com.example.syncid.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.delay

@Composable
fun AttendanceScannerScreen(
    onBackClick: () -> Unit,
    onNavigateToFullList: () -> Unit,
    viewModel: NfcViewModel
) {
    var scannedStudent by remember { mutableStateOf<String?>(null) }
    var attendanceCount by remember { mutableIntStateOf(24) }
    var hasMedicalAlert by remember { mutableStateOf(false) }

    // Simulación de escaneo para propósitos de demostración
    LaunchedEffect(Unit) {
        delay(3000)
        scannedStudent = "Diego Armando Maradona"
        hasMedicalAlert = true
        attendanceCount++
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. Placeholder de Cámara
        CameraPreviewPlaceholder(modifier = Modifier.fillMaxSize())

        // 2. Overlay de Escaneo (Marco)
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.Center)
                .border(BorderStroke(4.dp, Color.Cyan), RoundedCornerShape(24.dp))
        )

        // 3. Contador Flotante Superior (Ahora clickeable)
        Surface(
            onClick = onNavigateToFullList,
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.TopCenter),
            color = DarkSurface.copy(alpha = 0.8f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Groups, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Asistencia: $attendanceCount/30",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Visibility, contentDescription = "Ver Lista", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }

        // 4. Botón de Retroceso/Finalizar
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 16.dp)
                .background(DarkSurface.copy(alpha = 0.7f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
        }

        // 5. Tarjeta de Alumno Escaneado (Feedback)
        AnimatedVisibility(
            visible = scannedStudent != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Foto/Icono Alumno
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(AccentBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alumno Registrado", color = Color.Gray, fontSize = 12.sp)
                        Text(scannedStudent ?: "", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        
                        if (hasMedicalAlert) {
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Alerta Médica: Alergias", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Botón de confirmación rápida
                    IconButton(
                        onClick = { scannedStudent = null },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Green)
                    }
                }
            }
        }

        // Botón Finalizar en la base si no hay tarjeta
        if (scannedStudent == null) {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .height(56.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Finalizar Pase", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CameraPreviewPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.DarkGray)
            Spacer(Modifier.height(8.dp))
            Text("Iniciando cámara...", color = Color.DarkGray)
        }
    }
}
