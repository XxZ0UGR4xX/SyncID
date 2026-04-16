package com.example.syncid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.syncid.ui.navigation.SyncIDNavGraph
import com.example.syncid.ui.theme.SyncIDTheme
import com.example.syncid.ui.viewmodel.NfcViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NfcViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            
            SyncIDTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SyncIDNavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
