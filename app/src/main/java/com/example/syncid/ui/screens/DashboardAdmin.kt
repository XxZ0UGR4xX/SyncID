package com.example.syncid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- configuracion del tema (Colores) ---
val DarkSurface = Color(0xFF1A1C1E)
val AccentBlue = Color(0xFF2196F3)
val SuccessGreen = Color(0xFF4CAF50)

// --- modelos de datos ---
data class Alumno(
    val nombre: String,
    val control: String,
    val sangre: String,
    val pulseraActiva: Boolean
)

data class Pulsera(
    val id: String,
    val codigo: String,
    var estado: String
)

// --- (Simulación de Base de Datos) ---
// snapshots para que la UI reaccione a cambios en cualquier pantalla
val listaAlumnosGlobal = mutableStateListOf(
    Alumno("Kim Carrillo", "21110800", "O+", true),
    Alumno("Brenda Buenrostro", "21110801", "A-", false)
)

val listaPulserasGlobal = mutableStateListOf(
    Pulsera("1", "ID: 44:AA:22", "Disponible"),
    Pulsera("2", "ID: BB:11:CC", "Asignada")
)

@Composable
fun DashboardAdmin() {
    var pantallaActual by remember { mutableStateOf("dashboard") }

    when (pantallaActual) {
        "dashboard" -> MainDashboardContent(onNavigate = { pantallaActual = it })
        "directorio" -> DirectorioAlumnosScreen(onBack = { pantallaActual = "dashboard" })
        "pulseras" -> GestionPulserasScreen(onBack = { pantallaActual = "dashboard" })
        "reportes" -> ReportesSeguridadScreen(onBack = { pantallaActual = "dashboard" })
    }
}

// --- pantalla 1: dashboard principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardContent(onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control Administrador", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Text("Resumen del Campus", fontSize = 20.sp, fontWeight = FontWeight.Bold) }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("ALUMNOS DENTRO", "${listaAlumnosGlobal.filter { it.pulseraActiva }.size}", Icons.Default.People, Modifier.weight(1f))
                    StatCard("ACCESOS HOY", "2,450", Icons.AutoMirrored.Filled.Login, Modifier.weight(1f))
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Flujo de Entradas (07:00 - 13:00)", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        GraficaLineas(listOf(10f, 40f, 20f, 80f, 100f, 60f, 30f))
                    }
                }
            }

            item { Text("Gestión", fontWeight = FontWeight.Bold) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminMenuItem("Directorio de Alumnos", Icons.Default.ContactPage) { onNavigate("directorio") }
                    AdminMenuItem("Gestión de Pulseras", Icons.Default.SettingsInputAntenna) { onNavigate("pulseras") }
                    AdminMenuItem("Reportes de Seguridad", Icons.Default.Assessment) { onNavigate("reportes") }
                }
            }
        }
    }
}

// --- pantalla 2: directorio ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorioAlumnosScreen(onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var control by remember { mutableStateOf("") }
    var sangre by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Alumno") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = control, onValueChange = { control = it }, label = { Text("No. Control") })
                    OutlinedTextField(value = sangre, onValueChange = { sangre = it }, label = { Text("Tipo de Sangre") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nombre.isNotBlank()) {
                        listaAlumnosGlobal.add(Alumno(nombre, control, sangre, true))
                        showDialog = false; nombre = ""; control = ""; sangre = ""
                    }
                }) { Text("Añadir") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Directorio") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = AccentBlue) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            items(listaAlumnosGlobal) { alumno ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(alumno.nombre, fontWeight = FontWeight.Bold)
                            Text("Control: ${alumno.control}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Badge(containerColor = if (alumno.pulseraActiva) SuccessGreen else Color.Red) {
                            Text(if (alumno.pulseraActiva) "ACTIVA" else "INACTIVA", Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- pantalla 3: gestion de pulseras ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPulserasScreen(onBack: () -> Unit) {
    var pulseraAEditar by remember { mutableStateOf<Pulsera?>(null) }
    val opcionesEstado = listOf("Disponible", "Asignada", "Dañada", "Perdida")

    if (pulseraAEditar != null) {
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { pulseraAEditar = null },
            title = { Text("Actualizar Pulsera") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = pulseraAEditar!!.codigo,
                        onValueChange = { pulseraAEditar = pulseraAEditar!!.copy(codigo = it) },
                        label = { Text("ID de Pulsera") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Menú desplegable para el estado
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = pulseraAEditar!!.estado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estado de Asignación") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            opcionesEstado.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        pulseraAEditar = pulseraAEditar!!.copy(estado = opcion)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val index = listaPulserasGlobal.indexOfFirst { it.id == pulseraAEditar!!.id }
                    if (index != -1) listaPulserasGlobal[index] = pulseraAEditar!!
                    pulseraAEditar = null
                }) { Text("Guardar Cambios") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestión de Pulseras") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            })
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            items(listaPulserasGlobal) { pulsera ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { pulseraAEditar = pulsera },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(pulsera.codigo, fontWeight = FontWeight.Bold)
                            Text("Estado: ${pulsera.estado}", color = Color.Gray, fontSize = 13.sp)
                        }
                        IconButton(onClick = { listaPulserasGlobal.remove(pulsera) }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}



// --- pantalla 4: reportes ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesSeguridadScreen(onBack: () -> Unit) {
    val categoriasReporte = listOf(
        "Falla de Lectura" to Icons.Default.SdCardAlert,
        "Pulsera Extraviada" to Icons.Default.BluetoothDisabled,
        "Acceso Denegado" to Icons.Default.NoAccounts,
        "Intento de Forzado" to Icons.Default.Warning,
        "Mantenimiento de Sensor" to Icons.Default.Build
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reportes de Seguridad") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            })
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Text("Seleccione el tipo de incidencia:", fontSize = 14.sp, color = Color.Gray)
            }

            items(categoriasReporte) { (titulo, icono) ->
                OutlinedButton(
                    onClick = { /* Lógica para enviar reporte */ },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(icono, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(titulo)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { /* Alerta General */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.NotificationsActive, null)
                    Spacer(Modifier.width(8.dp))
                    Text("EMITIR ALERTA GENERAL DE CAMPUS")
                }
            }
        }
    }
}
// --- componentes auxiliares ---
@Composable
fun GraficaLineas(datos: List<Float>) {
    Canvas(Modifier.fillMaxWidth().height(120.dp)) {
        val distance = size.width / (datos.size - 1)
        datos.forEachIndexed { i, v ->
            val x = i * distance
            val y = size.height - (v / 100f * size.height)
            if (i > 0) {
                val prevX = (i - 1) * distance
                val prevY = size.height - (datos[i - 1] / 100f * size.height)
                drawLine(Color(0xFF2196F3), Offset(prevX, prevY), Offset(x, y), strokeWidth = 8f)
            }
            drawCircle(Color(0xFF2196F3), 12f, Offset(x, y))
        }
    }
}

@Composable
fun AdminMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AccentBlue)
            Spacer(Modifier.width(16.dp))
            Text(title)
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = SuccessGreen)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}