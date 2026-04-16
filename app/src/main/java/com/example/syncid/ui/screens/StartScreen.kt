package com.example.syncid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncid.ui.theme.AccentBlue
import com.example.syncid.ui.theme.DarkSurface

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.syncid.ui.viewmodel.NfcViewModel
import com.example.syncid.ui.utils.Translations

@Composable
fun StartScreen(viewModel: NfcViewModel, onRoleSelected: (String) -> Unit, onRegisterClick: () -> Unit) {
    val currentLang by viewModel.language.collectAsState()
    val t = { key: String -> Translations.getText(key, currentLang) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    val languages = listOf("Español", "English", "Français", "日本語", "Português")

    Box(modifier = Modifier.fillMaxSize()) {
        // Botón de Idioma en la esquina superior derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(onClick = { showLanguageMenu = true }) {
                Icon(Icons.Default.Language, contentDescription = "Change Language", tint = AccentBlue)
            }
            DropdownMenu(
                expanded = showLanguageMenu,
                onDismissRequest = { showLanguageMenu = false }
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang) },
                        onClick = {
                            viewModel.setLanguage(lang)
                            showLanguageMenu = false
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = t("welcome"),
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = AccentBlue
            )
            Text(
                text = "Instituto Tecnológico del Estado",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(t("select_profile"), fontSize = 16.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            RoleButton("USUARIO", onRoleSelected)
            Spacer(modifier = Modifier.height(12.dp))
            RoleButton("GUARDIA", onRoleSelected)
            Spacer(modifier = Modifier.height(12.dp))
            RoleButton("ADMINISTRADOR", onRoleSelected)

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onRegisterClick) {
                Text(
                    text = t("new_user_register"),
                    color = AccentBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RoleButton(role: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(role) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
    ) {
        Text(role, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
