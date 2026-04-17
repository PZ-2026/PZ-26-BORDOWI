package com.example.dentflow_android.data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dentflow_android.data.VisitViewModel

@Composable
fun VisitListScreen(viewModel: VisitViewModel, modifier: Modifier = Modifier) {
    // Teraz "visits" to lista obiektów VisitWithPatient
    val visits by viewModel.visits.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                Text(
                    text = "DentFlow - Lista Wizyt",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(visits) { item ->
                // Przekazujemy cały obiekt "item", który ma w sobie wizytę i pacjenta
                VisitItem(item)
            }
        }
    }
}

@Composable
fun VisitItem(item: VisitWithPatient) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Sprawdzamy, czy udało się pobrać dane pacjenta
            val patientName = if (item.patient != null) {
                "${item.patient.firstName} ${item.patient.lastName}"
            } else {
                "Ładowanie danych pacjenta..."
            }

            Text(
                text = patientName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Data: ${item.visit.visitDate}")
            Text(text = "Opis: ${item.visit.description}")

            Spacer(modifier = Modifier.height(8.dp))

            // Wyświetlamy status w ładniejszy sposób
            SuggestionChip(
                onClick = { /* opcjonalnie akcja */ },
                label = { Text(item.visit.status) }
            )
        }
    }
}