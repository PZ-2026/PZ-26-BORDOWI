package com.example.dentflow_android.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dentflow_android.data.ViewModel.TenantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTenantScreen(
    onBack: () -> Unit,
    tenantViewModel: TenantViewModel
) {
    // Stan dla każdego pola
    var name by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Polska") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zarejestruj Klinikę") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Dane podstawowe", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nazwa kliniki") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Nazwa placówki (np. Gabinet Główny)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Adres", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Ulica i numer") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = zip,
                    onValueChange = { zip = it },
                    label = { Text("Kod pocztowy") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Miasto") },
                    modifier = Modifier.weight(2f)
                )
            }

            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Kraj") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pokazujemy wskaźnik ładowania, jeśli ViewModel pracuje
            val isLoading by tenantViewModel.isLoading

            Button(
                onClick = {
                    // POPRAWKA: Przekazujemy WSZYSTKIE parametry do ViewModelu
                    tenantViewModel.registerClinic(
                        name = name,
                        locationName = if(locationName.isBlank()) "Placówka Główna" else locationName,
                        street = street,
                        city = city,
                        zip = zip,
                        country = country
                    )
                    onBack() // Wracamy do dashboardu, który automatycznie przełączy się na BusinessScreen dzięki State
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && street.isNotBlank() && city.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Utwórz i zarejestruj")
                }
            }
        }
    }
}