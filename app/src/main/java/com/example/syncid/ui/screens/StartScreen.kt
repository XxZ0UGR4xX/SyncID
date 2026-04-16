package com.example.syncid.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.*
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
            
            RoleButton(t("admin_role"), onClick = { onRoleSelected("ADMINISTRADOR") })
            Spacer(modifier = Modifier.height(12.dp))
            UserRoleDropdown(onRoleSelected, t)

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
fun UserRoleDropdown(onRoleSelected: (String) -> Unit, t: (String) -> String) {
    var expanded by remember { mutableStateOf(false) }
    val userRoles = listOf("ALUMNO", "MAESTRO", "GUARDIA")

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(t("user_role"), fontWeight = FontWeight.Bold, color = Color.White)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(DarkSurface)
        ) {
            userRoles.forEach { role ->
                DropdownMenuItem(
                    text = {
                        Text(
                            role,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    },
                    onClick = {
                        expanded = false
                        onRoleSelected(role)
                    }
                )
            }
        }
    }
}

@Composable
fun RoleButton(roleName: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
    ) {
        Text(roleName, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
