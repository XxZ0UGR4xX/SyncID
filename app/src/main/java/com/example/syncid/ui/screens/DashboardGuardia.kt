package com.example.syncid.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.*
import com.example.syncid.ui.viewmodel.NfcStatus
import com.example.syncid.ui.viewmodel.NfcViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardGuardia(viewModel: NfcViewModel) {
    var selectedTab by remember { mutableStateOf(1) } // Default to Scanner
    val tabs = listOf("Estacionamiento", "Escáner NFC", "Historial")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TEC-LINK Guardia", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Parking") },
                    icon = { Icon(Icons.Default.LocalParking, null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("NFC") },
                    icon = { Icon(Icons.Default.Nfc, null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Historial") },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ParkingPanel()
                1 -> NfcScannerScreen(viewModel)
                2 -> GuardHistory(viewModel)
            }
        }
    }
}

@Composable
fun ParkingPanel() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ocupación de Estacionamiento", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            Canvas(modifier = Modifier.size(180.dp)) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = AccentBlue,
                    startAngle = -90f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("45", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("disponibles", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Mapa de Lugares", fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(6) { col ->
                        val isOccupied = (row + col) % 3 != 0
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (isOccupied) Color.DarkGray else SuccessGreen.copy(alpha = 0.6f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NfcScannerScreen(viewModel: NfcViewModel) {
    val status by viewModel.nfcStatus.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "nfc")
    val radius by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val color = when (status) {
                    is NfcStatus.Success -> SuccessGreen
                    is NfcStatus.Error -> ErrorRed
                    else -> AccentBlue
                }
                drawCircle(
                    color = color.copy(alpha = 0.2f),
                    radius = radius.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            
            IconButton(
                onClick = { viewModel.simulateNfcScan() },
                modifier = Modifier.size(120.dp).background(DarkSurface, CircleShape)
            ) {
                Icon(
                    Icons.Default.Nfc,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = when (status) {
                        is NfcStatus.Success -> SuccessGreen
                        is NfcStatus.Error -> ErrorRed
                        else -> AccentBlue
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = when (status) {
                is NfcStatus.Idle -> "Acerque la pulsera al lector"
                is NfcStatus.Scanning -> "Escaneando..."
                is NfcStatus.Success -> (status as NfcStatus.Success).message
                is NfcStatus.Error -> (status as NfcStatus.Error).message
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = when (status) {
                is NfcStatus.Success -> SuccessGreen
                is NfcStatus.Error -> ErrorRed
                else -> Color.White
            }
        )
    }
}

@Composable
fun GuardHistory(viewModel: NfcViewModel) {
    val history by viewModel.accessHistory.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Historial de Accesos Local", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(history) { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(record.userName, fontWeight = FontWeight.Bold)
                        Text("${record.location} • ${record.timestamp}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
