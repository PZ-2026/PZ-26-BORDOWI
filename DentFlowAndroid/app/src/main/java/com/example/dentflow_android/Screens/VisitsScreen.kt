package com.example.dentflow_android.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dentflow_android.data.ViewModel.AppointmentViewModel
import com.example.dentflow_android.data.remote.AppointmentResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VisitsScreen(
    // USUNIĘTO: tenantId i userRole z parametrów - ViewModel sam je pobierze
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Pobieramy rolę z ViewModelu (jeśli ją tam zapisałeś) lub domyślnie "PATIENT"
    val userRole = "PATIENT"

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Pobieranie danych przy wejściu lub zmianie daty
    LaunchedEffect(selectedDate) {
        viewModel.fetchAppointments(selectedDate)
    }

    Scaffold(
        floatingActionButton = {
            if (userRole != "PATIENT") {
                FloatingActionButton(
                    onClick = { /* Otwórz okno dodawania wizyty */ },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // NAGŁÓWEK EKRANU
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (userRole == "PATIENT") "Moje Wizyty" else "Terminarz Kliniki",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pl"))),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            // POZIOMY KALENDARZ (15 dni od dzisiaj)
            val days = remember { (0..14).map { LocalDate.now().plusDays(it.toLong()) } }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(days) { date ->
                    DayItem(
                        date = date,
                        isSelected = selectedDate == date,
                        onSelect = { selectedDate = date }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // LISTA WIZYT
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (appointments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak wizyt na ten dzień", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appointments) { appointment ->
                        UniversalVisitCard(appointment, userRole)
                    }
                }
            }
        }
    }
}

@Composable
fun DayItem(date: LocalDate, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(85.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("E", Locale("pl"))),
                fontSize = 12.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun UniversalVisitCard(appointment: AppointmentResponse, userRole: String) {
    val timeDisplay = try {
        appointment.startAt.substring(11, 16)
    } catch (e: Exception) {
        "--:--"
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = timeDisplay,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(55.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (userRole == "PATIENT") "Klinika DentFlow" else "Pacjent ID: ${appointment.patientId}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Usługa ID: ${appointment.serviceItemId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val statusColor = when (appointment.status.uppercase()) {
                    "CONFIRMED" -> Color(0xFF4CAF50)
                    "PENDING" -> Color(0xFFFF9800)
                    "COMPLETED" -> Color.Gray
                    else -> MaterialTheme.colorScheme.outline
                }
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(statusColor))
            }
        }
    }
}