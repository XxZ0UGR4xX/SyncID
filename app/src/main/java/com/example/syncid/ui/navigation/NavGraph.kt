package com.example.syncid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.syncid.ui.screens.*
import com.example.syncid.ui.viewmodel.NfcViewModel
import com.example.syncid.data.UserRole
import com.example.teclink.ui.screens.DashboardAdmin

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Start : Screen("start")
    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object Registration : Screen("registration")
    object UserDashboard : Screen("user_dashboard")
    object TeacherDashboard : Screen("teacher_dashboard")
    object GuardDashboard : Screen("guard_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object AttendanceScanner : Screen("attendance_scanner")
    object AttendanceList : Screen("attendance_list")
}

sealed class StudentScreen(val route: String) {
    object Dashboard : StudentScreen("student_dashboard")
    object History : StudentScreen("student_history")
    object MedicalData : StudentScreen("student_medical_data")
    object VirtualCard : StudentScreen("student_virtual_card")
    object ReportLoss : StudentScreen("student_report_loss")
    object EmergencyScan : StudentScreen("emergency_scan")
    object PeerMedicalInfo : StudentScreen("peer_medical_info")
    object Profile : StudentScreen("student_profile")
    object Settings : StudentScreen("settings")
}

@Composable
fun SyncIDNavGraph(navController: NavHostController, viewModel: NfcViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Start.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Start.route) {
            StartScreen(
                viewModel = viewModel,
                onRoleSelected = { role ->
                    navController.navigate(Screen.Login.createRoute(role))
                },
                onRegisterClick = {
                    navController.navigate(Screen.Registration.route)
                }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onBackClick = { navController.popBackStack() },
                onNext = {
                    // Por ahora, después del registro simulamos éxito y vamos al login de Usuario
                    navController.navigate(Screen.Login.createRoute("USUARIO")) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "USUARIO"
            LoginScreen(
                viewModel = viewModel,
                role = role,
                onLoginSuccess = {
                    val destination = when (role) {
                        "ALUMNO", "USUARIO" -> {
                            viewModel.updateUserRole(UserRole.USUARIO)
                            StudentScreen.Dashboard.route
                        }
                        "MAESTRO" -> {
                            viewModel.updateUserRole(UserRole.MAESTRO)
                            Screen.TeacherDashboard.route
                        }
                        "GUARDIA" -> {
                            viewModel.updateUserRole(UserRole.GUARDIA)
                            Screen.GuardDashboard.route
                        }
                        "ADMINISTRADOR" -> {
                            viewModel.updateUserRole(UserRole.ADMINISTRADOR)
                            Screen.AdminDashboard.route
                        }
                        else -> StudentScreen.Dashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        // Rutas del Alumno
        composable(StudentScreen.Dashboard.route) {
            DashboardUser(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate(StudentScreen.History.route) },
                onNavigateToMedicalData = { navController.navigate(StudentScreen.MedicalData.route) },
                onNavigateToVirtualCard = { navController.navigate(StudentScreen.VirtualCard.route) },
                onNavigateToReportLoss = { navController.navigate(StudentScreen.ReportLoss.route) },
                onNavigateToEmergencyScan = { navController.navigate(StudentScreen.EmergencyScan.route) },
                onNavigateToProfile = { navController.navigate(StudentScreen.Profile.route) }
            )
        }
        
        composable(StudentScreen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(StudentScreen.MedicalData.route) {
            MedicalDataScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(StudentScreen.VirtualCard.route) {
            VirtualCardScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(StudentScreen.ReportLoss.route) {
            ReportLossScreen(onBack = { navController.popBackStack() })
        }

        composable(StudentScreen.EmergencyScan.route) {
            EmergencyScanScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onScanSuccess = {
                    navController.navigate(StudentScreen.PeerMedicalInfo.route)
                }
            )
        }

        composable(StudentScreen.PeerMedicalInfo.route) {
            PeerMedicalInfoScreen(
                onClose = {
                    navController.navigate(StudentScreen.Dashboard.route) {
                        popUpTo(StudentScreen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(StudentScreen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateToMedicalData = { navController.navigate(StudentScreen.MedicalData.route) },
                onNavigateToHistory = { navController.navigate(StudentScreen.History.route) },
                onNavigateToDashboard = { navController.navigate(StudentScreen.Dashboard.route) },
                onNavigateToEmergencyScan = { navController.navigate(StudentScreen.EmergencyScan.route) },
                onNavigateToSettings = { navController.navigate(StudentScreen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Start.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(StudentScreen.Settings.route) {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        
        composable(Screen.TeacherDashboard.route) {
            DashboardTeacher(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate(StudentScreen.History.route) },
                onNavigateToScanner = { navController.navigate(Screen.AttendanceScanner.route) },
                onNavigateToEmergencyScan = { navController.navigate(StudentScreen.EmergencyScan.route) },
                onNavigateToProfile = { navController.navigate(StudentScreen.Profile.route) }
            )
        }

        composable(Screen.AttendanceScanner.route) {
            AttendanceScannerScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToFullList = { navController.navigate(Screen.AttendanceList.route) },
                viewModel = viewModel
            )
        }

        composable(Screen.AttendanceList.route) {
            AttendanceListScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // Otras rutas
        composable(Screen.GuardDashboard.route) {
            DashboardGuardia(viewModel)
        }
        
        composable(Screen.AdminDashboard.route) {
            DashboardAdmin()
        }
    }
}
