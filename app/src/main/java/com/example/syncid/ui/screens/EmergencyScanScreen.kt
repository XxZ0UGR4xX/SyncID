package com.example.syncid.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.*
import com.example.syncid.ui.viewmodel.NfcStatus
import com.example.syncid.ui.viewmodel.NfcViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScanScreen(
    viewModel: NfcViewModel,
    onBackClick: () -> Unit,
    onScanSuccess: () -> Unit
) {
    val status by viewModel.nfcStatus.collectAsState()

    // Simulación de escaneo para propósitos de la demo
    LaunchedEffect(status) {
        if (status is NfcStatus.Success) {
            delay(1500)
            onScanSuccess()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "nfc")
    val radius by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 160f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escaneo de Emergencia", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
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
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "ESCANEANDO PULSERA DE COMPAÑERO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = AccentBlue
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Mantén el dispositivo cerca de la pulsera en caso de emergencia",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(64.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(320.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val color = when (status) {
                        is NfcStatus.Success -> SuccessGreen
                        is NfcStatus.Error -> ErrorRed
                        else -> AccentBlue
                    }
                    drawCircle(
                        color = color.copy(alpha = 0.15f),
                        radius = radius.dp.toPx(),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = color.copy(alpha = 0.3f),
                        radius = (radius * 0.7f).dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                
                Surface(
                    onClick = { viewModel.simulateNfcScan() },
                    modifier = Modifier.size(140.dp),
                    shape = CircleShape,
                    color = DarkSurface,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Nfc,
                            contentDescription = "Escanear",
                            modifier = Modifier.size(72.dp),
                            tint = when (status) {
                                is NfcStatus.Success -> SuccessGreen
                                is NfcStatus.Error -> ErrorRed
                                else -> AccentBlue
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            if (status is NfcStatus.Scanning || status is NfcStatus.Success) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = if (status is NfcStatus.Success) SuccessGreen else AccentBlue,
                    strokeWidth = 4.dp
                )
            }

            Text(
                text = when (status) {
                    is NfcStatus.Idle -> "Listo para escanear"
                    is NfcStatus.Scanning -> "Procesando pulsera..."
                    is NfcStatus.Success -> "¡Pulsera Identificada!"
                    is NfcStatus.Error -> (status as NfcStatus.Error).message
                },
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = when (status) {
                    is NfcStatus.Success -> SuccessGreen
                    is NfcStatus.Error -> ErrorRed
                    else -> Color.White
                }
            )
        }
    }
}
