package com.example.syncid.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.AccentBlue
import com.example.syncid.ui.theme.DarkSurface
import com.example.syncid.ui.theme.SuccessGreen
import com.example.syncid.ui.viewmodel.NfcViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceListScreen(
    onBackClick: () -> Unit,
    viewModel: NfcViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Presentes", "Faltantes")

    val presentStudents = listOf(
        "Carlos Mendoza" to "07:55 AM",
        "Ana Lucía Silva" to "07:58 AM",
        "Roberto Gómez" to "08:02 AM",
        "Diego Maradona" to "08:10 AM"
    )

    val missingStudents = listOf(
        "Juan Pérez",
        "María García",
        "Pedro López",
        "Lucía Fernández"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Asistencia", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Buscar alumno...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = AccentBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AccentBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Lista
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedTab == 0) {
                    items(presentStudents.filter { it.first.contains(searchQuery, ignoreCase = true) }) { (name, time) ->
                        AttendanceItem(name, time, isPresent = true)
                    }
                } else {
                    items(missingStudents.filter { it.contains(searchQuery, ignoreCase = true) }) { name ->
                        AttendanceItem(name, null, isPresent = false)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceItem(name: String, time: String?, isPresent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPresent) SuccessGreen.copy(alpha = 0.05f) else Color.Red.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, if (isPresent) SuccessGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(if (isPresent) SuccessGreen.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPresent) Icons.Default.CheckCircle else Icons.Default.PersonOff,
                    contentDescription = null,
                    tint = if (isPresent) SuccessGreen else Color.Red
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color.White)
                if (time != null) {
                    Text("Escaneado: $time", fontSize = 12.sp, color = Color.Gray)
                } else {
                    Text("Sin registro", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (!isPresent) {
                Button(
                    onClick = { /* Pase manual */ },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pase Manual", fontSize = 11.sp)
                }
            }
        }
    }
}
