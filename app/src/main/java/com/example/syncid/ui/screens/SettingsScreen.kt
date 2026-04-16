package com.example.syncid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun SettingsScreen(viewModel: NfcViewModel, onBack: () -> Unit) {
    val currentLang by viewModel.language.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val languages = listOf("Español", "English", "Français", "日本語", "Português")

    val t = { key: String -> Translations.getText(key, currentLang) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("settings"), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDarkTheme) DarkSurface else AccentBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Text(
                "Preferencias",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingsItem(
                title = t("language"),
                subtitle = currentLang,
                icon = Icons.Default.Language,
                onClick = { showLanguageDialog = true }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingsItem(
                title = t("notifications"),
                subtitle = if (notificationsEnabled) "Activadas" else "Desactivadas",
                icon = Icons.Default.Notifications,
                onClick = { showNotificationDialog = true }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tema de la App Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = AccentBlue)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(t("theme"), fontWeight = FontWeight.Medium)
                        Text(if (isDarkTheme) "Oscuro" else "Claro", color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Acerca de",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SettingsItem(
                title = t("version"),
                subtitle = "1.0.0 (Beta)",
                icon = Icons.Default.Info,
                onClick = { }
            )
        }

        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text(t("language"), color = MaterialTheme.colorScheme.onSurface) },
                text = {
                    Column {
                        languages.forEach { language ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setLanguage(language)
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (language == currentLang),
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(language, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Cerrar", color = AccentBlue)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        if (showNotificationDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationDialog = false },
                title = { Text(t("notifications"), color = MaterialTheme.colorScheme.onSurface) },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activar notificaciones push", color = MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showNotificationDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Aceptar", color = Color.White)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun SettingsItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AccentBlue)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
