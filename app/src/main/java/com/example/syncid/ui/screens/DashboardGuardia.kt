package com.example.syncid.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.data.*
import com.example.syncid.ui.theme.*
import com.example.syncid.ui.viewmodel.NfcStatus
import com.example.syncid.ui.viewmodel.NfcViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardGuardia(viewModel: NfcViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Resumen", "Escáner", "Historial", "Parking", "Excursión", "Alertas")
    val icons = listOf(
        Icons.Default.Dashboard,
        Icons.Default.Nfc,
        Icons.AutoMirrored.Filled.List,
        Icons.Default.LocalParking,
        Icons.Default.Explore,
        Icons.Default.Notifications
    )

    val alerts by viewModel.alerts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("TEC-LINK Guardia", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Sistema Operativo", fontSize = 12.sp, color = SuccessGreen)
                    }
                },
                actions = {
                    BadgedBox(badge = {
                        if (alerts.isNotEmpty()) {
                            Badge { Text(alerts.size.toString()) }
                        }
                    }) {
                        Icon(
                            Icons.Default.Notifications, 
                            contentDescription = "Alertas", 
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { selectedTab = 5 }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface, tonalElevation = 8.dp) {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(label, fontSize = 9.sp, maxLines = 1) },
                        icon = { Icon(icons[index], null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            selectedTextColor = AccentBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = AccentBlue.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(DarkBg)) {
            when (selectedTab) {
                0 -> SummaryPanel(viewModel, onAlertsClick = { selectedTab = 5 })
                1 -> NfcScannerScreen(viewModel)
                2 -> GuardHistory(viewModel)
                3 -> ParkingPanel(viewModel)
                4 -> ExcursionPanel(viewModel)
                5 -> AlertsPanel(viewModel)
            }
            
            // Global Alert Overlay for Critical Alerts
            val criticalAlert = alerts.firstOrNull { it.level == AlertLevel.CRITICAL || it.type == "SOS" }
            if (criticalAlert != null) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopCenter) {
                    SwipeToDismissAlert(
                        alert = criticalAlert,
                        onDismiss = { viewModel.removeAlert(criticalAlert.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissAlert(alert: SystemAlert, onDismiss: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { Box(Modifier.fillMaxSize()) },
        content = {
            CriticalAlertToast(alert)
        }
    )
}

@Composable
fun SummaryPanel(viewModel: NfcViewModel, onAlertsClick: () -> Unit) {
    val studentsInside by viewModel.studentsInside.collectAsState()
    val entries by viewModel.dailyEntries.collectAsState()
    val exits by viewModel.dailyExits.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Resumen en Tiempo Real", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Alumnos In", studentsInside.toString(), Icons.Default.Groups, AccentBlue, modifier = Modifier.weight(1f))
                StatCard(
                    label = "Alertas Hoy", 
                    value = alerts.size.toString(), 
                    icon = Icons.Default.Warning, 
                    color = ErrorRed, 
                    modifier = Modifier.weight(1f),
                    onClick = onAlertsClick
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Actividad del Día", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ActivityStat("Entradas", entries.toString(), SuccessGreen)
                        ActivityStat("Salidas", exits.toString(), AccentBlue)
                    }
                }
            }
        }

        item {
            Text("Alertas Recientes", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        items(alerts.take(3)) { alert ->
            AlertItem(alert)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ActivityStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AlertItem(alert: SystemAlert) {
    val color = when (alert.level) {
        AlertLevel.CRITICAL -> ErrorRed
        AlertLevel.WARNING -> WarningYellow
        AlertLevel.INFO -> SuccessGreen
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                when(alert.type) {
                    "SOS" -> Icons.Default.Sos
                    "Unauthorized" -> Icons.Default.Block
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(alert.title, fontWeight = FontWeight.Bold, color = color)
                Text(alert.message, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                Text(alert.timestamp, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun NfcScannerScreen(viewModel: NfcViewModel) {
    val status by viewModel.nfcStatus.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
            // Animated Circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val color = when (status) {
                    is NfcStatus.Success -> SuccessGreen
                    is NfcStatus.Error -> ErrorRed
                    is NfcStatus.Scanning -> AccentBlue
                    else -> Color.Gray
                }
                drawCircle(
                    color = color.copy(alpha = 0.15f),
                    radius = (100 * pulseScale).dp.toPx()
                )
                drawCircle(
                    color = color.copy(alpha = 0.4f),
                    radius = 80.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            Surface(
                onClick = { viewModel.simulateNfcScan() },
                shape = CircleShape,
                color = DarkSurface,
                tonalElevation = 4.dp,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Nfc,
                        contentDescription = "Escanear",
                        modifier = Modifier.size(56.dp),
                        tint = when (status) {
                            is NfcStatus.Success -> SuccessGreen
                            is NfcStatus.Error -> ErrorRed
                            else -> AccentBlue
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedContent(targetState = status, label = "status_info") { currentStatus ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (currentStatus) {
                    is NfcStatus.Idle -> {
                        Text("Listo para Escanear", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Acerque la pulsera al sensor NFC", color = Color.Gray)
                    }
                    is NfcStatus.Scanning -> {
                        CircularProgressIndicator(color = AccentBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Validando Identidad...", fontWeight = FontWeight.Medium)
                    }
                    is NfcStatus.Success -> {
                        val student = currentStatus.student
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(48.dp))
                            Text("ACCESO PERMITIDO", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            student?.let {
                                Card(
                                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(50.dp).background(AccentBlue.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                            Text(it.name.take(1), fontWeight = FontWeight.Bold, color = AccentBlue)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(it.name, fontWeight = FontWeight.Bold)
                                            Text("Grupo: ${it.group} • ID: ${it.controlNumber}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is NfcStatus.Error -> {
                        Icon(Icons.Default.Error, null, tint = ErrorRed, modifier = Modifier.size(48.dp))
                        Text("ACCESO DENEGADO", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text(currentStatus.message, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun GuardHistory(viewModel: NfcViewModel) {
    val history by viewModel.accessHistory.collectAsState()
    var filterType by rememberSaveable { mutableStateOf("Todos") }
    val filters = listOf("Todos", "Entrada", "Salida", "Error")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial de Accesos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = filterType == filter,
                    onClick = { filterType = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val filteredHistory = if (filterType == "Todos") history else history.filter { it.type == filterType }
            
            items(filteredHistory) { record ->
                AccessRecordCard(record)
            }
        }
    }
}

@Composable
fun AccessRecordCard(record: AccessRecord) {
    val iconColor = when(record.type) {
        "Entrada" -> SuccessGreen
        "Salida" -> AccentBlue
        else -> ErrorRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                    when(record.type) {
                        "Entrada" -> Icons.AutoMirrored.Filled.Login
                        "Salida" -> Icons.AutoMirrored.Filled.Logout
                        else -> Icons.Default.Report
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(record.userName, fontWeight = FontWeight.Bold)
                Text(record.location, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(record.timestamp, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text(record.status, fontSize = 10.sp, color = if (record.status == "Permitido") SuccessGreen else ErrorRed)
            }
        }
    }
}

@Composable
fun ParkingPanel(viewModel: NfcViewModel) {
    val spots by viewModel.parkingSpots.collectAsState()
    val availableCount = spots.count { !it.isOccupied }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Módulo de Estacionamiento", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    CircularProgressIndicator(
                        progress = { availableCount.toFloat() / spots.size },
                        modifier = Modifier.fillMaxSize(),
                        color = SuccessGreen,
                        strokeWidth = 8.dp,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    Text("$availableCount", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    Text("Lugares Disponibles", fontWeight = FontWeight.Medium)
                    Text("Total: ${spots.size} cajones", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Text("Mapa de Disponibilidad (Toque para simular)", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(spots) { spot ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (spot.isOccupied) ErrorRed.copy(alpha = 0.6f) else SuccessGreen.copy(alpha = 0.6f))
                        .clickable { viewModel.toggleParkingSpot(spot.id) }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DirectionsCar, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Text("P-${spot.id + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ExcursionPanel(viewModel: NfcViewModel) {
    val students by viewModel.excursionStudents.collectAsState()
    val presentCount = students.count { it.isPresent }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo Excursión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Badge(containerColor = WarningYellow) {
                Text("GPS ACTIVO", color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ActivityStat("Total", students.size.toString(), Color.White)
                ActivityStat("Presentes", presentCount.toString(), SuccessGreen)
                ActivityStat("Faltantes", (students.size - presentCount).toString(), ErrorRed)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Lista de Alumnos", fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
            items(students) { student ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (student.isPresent) Icons.Default.Person else Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = if (student.isPresent) SuccessGreen else ErrorRed
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, fontWeight = FontWeight.Bold)
                            Text(student.lastKnownLocation ?: "Ubicación desconocida", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = student.isPresent,
                            onCheckedChange = { viewModel.toggleExcursionStudent(student.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertsPanel(viewModel: NfcViewModel) {
    val alerts by viewModel.alerts.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Centro de Alertas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Gestión de incidencias y avisos del sistema", fontSize = 12.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(16.dp))

        if (alerts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay alertas activas", fontWeight = FontWeight.Bold)
                    Text("El campus está operando con normalidad", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(alerts) { alert ->
                    SwipeToDismissAlert(
                        alert = alert,
                        onDismiss = { viewModel.removeAlert(alert.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CriticalAlertToast(alert: SystemAlert) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ErrorRed),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ReportProblem, null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(alert.title, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(alert.message, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}
