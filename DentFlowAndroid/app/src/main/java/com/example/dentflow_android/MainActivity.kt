package com.example.dentflow_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dentflow_android.ui.theme.DentFlowAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dentflow_android.Screens.*
import com.example.dentflow_android.data.ViewModel.NotificationViewModel
import com.example.dentflow_android.data.ViewModel.StaffViewModel
import com.example.dentflow_android.data.ViewModel.TenantViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }

            DentFlowAndroidTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { tenantId ->
                                // Docelowo zapisz tenantId i userId w SharedPreferences lub DataStore
                                navController.navigate("main_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = { navController.navigate("register") }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { navController.navigate("login") },
                            onBackToLogin = { navController.popBackStack() }
                        )
                    }

                    composable("main_dashboard") {
                        MainDashboard(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDarkTheme = it },
                            navController = navController
                        )
                    }

                    composable("staff_management") {
                        StaffManagementScreen(onBackClick = { navController.popBackStack() })
                    }

                    composable("patient_list") {
                        PatientListScreen(onBackClick = { navController.popBackStack() })
                    }

                    composable("appointment_setup/{staffId}") { backStackEntry ->
                        val staffId = backStackEntry.arguments?.getString("staffId")?.toLongOrNull() ?: 0L

                        Column(modifier = Modifier.fillMaxSize().padding(32.dp).statusBarsPadding()) {
                            Text(
                                text = "Rezerwacja wizyty",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Wybrany lekarz ID: $staffId")
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Wróć do listy")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboard(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    navController: NavHostController,
    staffViewModel: StaffViewModel = hiltViewModel(),
    tenantViewModel: TenantViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var isShowingSettings by remember { mutableStateOf(false) }

    // Dane do testów (docelowo pobierz z SharedPreferences po logowaniu)
    val currentTenantId = 1L
    val currentUserId = 5L

    // Obserwowanie danych (używamy collectAsState dla Flow, aby uniknąć lagów)
    val staffList by staffViewModel.staffMembers.collectAsState()
    val tenantData by tenantViewModel.tenantState
    val serviceList by remember { derivedStateOf { tenantViewModel.servicesState.value } }

    // Inicjalizacja danych - LaunchedEffect gwarantuje, że nie zablokujemy UI
    LaunchedEffect(Unit) {
        staffViewModel.loadStaff(currentTenantId)
        tenantViewModel.loadAllTenantData(currentTenantId)
        notificationViewModel.fetchNotifications(currentTenantId, currentUserId)
    }

    val items = listOf("Home", "Firma", "Admin", "Grafik", "Alarmy", "Konto")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Business,
        Icons.Default.AdminPanelSettings,
        Icons.Default.CalendarMonth, // Zmiana ikony na bardziej pasującą do grafiku
        Icons.Default.Notifications,
        Icons.Default.AccountCircle
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            BadgedBox(badge = {
                                // Pokazujemy kropkę przy "Alarmach" jeśli są nieprzeczytane
                                if (index == 4) {
                                    val unreadCount by notificationViewModel.unreadCount.collectAsState()
                                    if (unreadCount > 0) {
                                        Badge { Text(unreadCount.toString()) }
                                    }
                                }
                            }) {
                                Icon(icons[index], contentDescription = item)
                            }
                        },
                        label = { Text(item, fontSize = 10.sp) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (index != 5) isShowingSettings = false
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> HomeScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    staffList = staffList,
                    serviceList = serviceList,
                    tenantData = tenantData,
                    onStaffClick = { staff ->
                        navController.navigate("appointment_setup/${staff.id}")
                    }
                )
                1 -> BusinessScreen()
                2 -> AdminPanelScreen(
                    onNavigateToStaff = { navController.navigate("staff_management") },
                    onNavigateToPatients = { navController.navigate("patient_list") }
                )
                3 -> ScheduleScreen(
                    viewModel = hiltViewModel(),
                    role = "ADMIN", // Docelowo pobierz z sesji
                    userId = currentUserId
                )
                4 -> NotificationsScreen(
                    tenantId = currentTenantId,
                    userId = currentUserId,
                    viewModel = notificationViewModel
                )
                5 -> {
                    if (!isShowingSettings) {
                        AccountScreen(
                            onSettingsClick = { isShowingSettings = true },
                            onLogoutClick = {
                                navController.navigate("login") {
                                    popUpTo("main_dashboard") { inclusive = true }
                                }
                            },
                            onEditBusinessClick = { selectedItem = 1 }
                        )
                    } else {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = onThemeChange,
                            onBackClick = { isShowingSettings = false }
                        )
                    }
                }
            }
        }
    }
}