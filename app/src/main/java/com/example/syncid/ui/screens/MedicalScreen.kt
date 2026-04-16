package com.example.syncid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDataScreen(onBackClick: () -> Unit) {
    BackHandler {
        onBackClick()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos Médicos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { }) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar Datos", color = AccentBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Banner Informativo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AccentBlue.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AccentBlue)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Esta información es estrictamente confidencial y solo se utilizará en caso de emergencia médica.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grupo Sanguíneo
            Text("Grupo Sanguíneo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
            var selectedType by remember { mutableStateOf("O+") }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(bloodTypes) { type ->
                    val isSelected = type == selectedType
                    OutlinedCard(
                        onClick = { selectedType = type },
                        modifier = Modifier.height(45.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AccentBlue.copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(type, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de Texto
            MedicalTextField(label = "Alergias", value = "Ninguna conocida")
            Spacer(modifier = Modifier.height(16.dp))
            MedicalTextField(label = "Padecimientos Crónicos", value = "Asma estacional controlada")

            Spacer(modifier = Modifier.height(32.dp))

            // Contacto de Emergencia
            Text("Contacto de Emergencia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EmergencyField(label = "Nombre", value = "María Elena Rodríguez")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))
                    EmergencyField(label = "Parentesco", value = "Madre")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))
                    EmergencyField(label = "Teléfono", value = "55-1234-1")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Última actualización: 12 de Octubre, 2023",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MedicalTextField(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun EmergencyField(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
