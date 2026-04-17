package com.example.dentflow_android

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dentflow_android.data.remote.PatientResponse
// Jeśli masz ViewModel w innym pakiecie, odkomentuj i popraw:
// import com.example.dentflow_android.ui.viewmodels.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    onBackClick: () -> Unit,
    viewModel: PatientViewModel = hiltViewModel()
) {
    val patients by viewModel.patients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // Obserwujemy stan ładowania

    LaunchedEffect(Unit) {
        viewModel.loadPatients(1L)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Baza Pacjentów", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Szukaj */ }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Tu będzie otwieranie dialogu dodawania */ }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj pacjenta")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                // Pokazuj kręciołek TYLKO gdy trwa ładowanie
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (patients.isEmpty()) {
                // Pokazuj komunikat, gdy ładowanie się skończyło i lista jest pusta
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Brak pacjentów w bazie",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                // Wyświetl listę, gdy są dane
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(patients) { patient ->
                        PatientItem(patient = patient)
                    }
                }
            }
        }
    }
}

@Composable
fun PatientItem(patient: PatientResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Szczegóły */ },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${patient.firstName} ${patient.lastName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Email: ${patient.email ?: "Brak danych"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}