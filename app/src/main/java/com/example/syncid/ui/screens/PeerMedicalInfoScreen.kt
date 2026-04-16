package com.example.syncid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerMedicalInfoScreen(
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información de Emergencia", color = Color.White) },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ErrorRed)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Perfil del Compañero
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f))
                            .border(3.dp, ErrorRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "CARLOS EDUARDO MÉNDEZ RUIZ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Ingeniería Mecatrónica • 7mo Semestre",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DATOS MÉDICOS CRÍTICOS
            Text(
                "DATOS MÉDICOS CRÍTICOS",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                color = ErrorRed,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(2.dp, ErrorRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TIPO DE SANGRE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                            Text("O POSITIVO (O+)", fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                        Icon(Icons.Default.Bloodtype, contentDescription = null, modifier = Modifier.size(48.dp), tint = ErrorRed)
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ErrorRed.copy(alpha = 0.2f))
                    
                    MedicalInfoRow(label = "ALERGIAS", value = "PENICILINA, MARISCOS", icon = Icons.Default.Warning)
                    Spacer(modifier = Modifier.height(12.dp))
                    MedicalInfoRow(label = "PADECIMIENTOS", value = "ASMA ESTACIONAL", icon = Icons.Default.MedicalInformation)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTACTOS DE EMERGENCIA
            Text(
                "ACCIONES DE EMERGENCIA",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Simular llamada */ },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text("LLAMAR A CONTACTO", fontWeight = FontWeight.Bold)
                    Text("Madre: 55-1234-5678", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Simular llamada */ },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.LocalHospital, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text("SERVICIOS MÉDICOS CAMPUS", fontWeight = FontWeight.Bold)
                    Text("Ext. 4000 / Emergencias", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
            ) {
                Text("CERRAR Y FINALIZAR ATENCIÓN", color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MedicalInfoRow(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = ErrorRed)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
