package com.example.syncid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.AccentBlue
import com.example.syncid.ui.theme.DarkSurface
import com.example.syncid.ui.theme.SuccessGreen
import com.example.syncid.ui.utils.Translations
import com.example.syncid.ui.viewmodel.NfcViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: NfcViewModel, onBackClick: () -> Unit) {
    val currentLang by viewModel.language.collectAsState()
    val t = { key: String -> Translations.getText(key, currentLang) }
    val context = LocalContext.current

    BackHandler {
        onBackClick()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("history_title"), color = Color.White) },
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
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.White)
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
                .padding(16.dp)
        ) {
            // Resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(t("period_actual"), color = Color.Gray, fontSize = 12.sp)
                        Text("Octubre 2023", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(t("filter"))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KPIs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    label = t("entries_today"),
                    value = "04",
                    containerColor = AccentBlue.copy(alpha = 0.2f),
                    contentColor = AccentBlue,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    label = t("last_location"),
                    value = "Biblioteca Central",
                    containerColor = SuccessGreen.copy(alpha = 0.2f),
                    contentColor = SuccessGreen,
                    modifier = Modifier.weight(1.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(t("recent_records"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            val dummyRecords = listOf(
                AccessItemData("Puerta Principal Norte", "Entrada", "Hoy, 24 Oct | 07:45 AM", true),
                AccessItemData("Estacionamiento E3", "Salida", "Hoy, 24 Oct | 13:20 PM", false),
                AccessItemData("Laboratorio de Computo", "Entrada", "Ayer, 23 Oct | 09:15 AM", true),
                AccessItemData("Gimnasio", "Entrada", "Ayer, 23 Oct | 17:00 PM", true)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(dummyRecords) { record ->
                    AccessRecordCard(record)
                }
            }
        }
    }
}

@Composable
fun KpiCard(label: String, value: String, containerColor: Color, contentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = contentColor)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White, maxLines = 1)
        }
    }
}

data class AccessItemData(val place: String, val type: String, val dateTime: String, val isEntry: Boolean)

@Composable
fun AccessRecordCard(data: AccessItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (data.isEntry) SuccessGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (data.isEntry) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = if (data.isEntry) SuccessGreen else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(data.place, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        data.type,
                        color = if (data.isEntry) SuccessGreen else Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Text(" " + data.dateTime.split("|")[0], fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Text(" " + data.dateTime.split("|")[1], fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}
