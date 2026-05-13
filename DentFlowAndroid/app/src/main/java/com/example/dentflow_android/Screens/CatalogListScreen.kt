package com.example.dentflow_android.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dentflow_android.data.ViewModel.TenantViewModel
import com.example.dentflow_android.data.remote.ServiceCatalogItemDTO


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogListScreen(
    onBackClick: () -> Unit,
    tenantViewModel: TenantViewModel = hiltViewModel()
) {
    val services by tenantViewModel.servicesState
    val isLoading by tenantViewModel.isLoading

    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<ServiceCatalogItemDTO?>(null) }

    LaunchedEffect(Unit) {
        tenantViewModel.loadServices()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cennik Usług") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        selectedService = null
                        showAddEditDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj usługę")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading && services.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (services.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ContentPasteSearch, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Brak usług w cenniku", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(services) { service ->
                        ServiceItemCard(
                            service = service,
                            onEdit = {
                                selectedService = service
                                showAddEditDialog = true
                            },
                            onDelete = { tenantViewModel.deleteService(service.id) }
                        )
                    }
                }
            }
        }

        if (showAddEditDialog) {
            ServiceAddEditDialog(
                service = selectedService,
                onDismiss = { showAddEditDialog = false },
                onConfirm = { name, priceCents, duration, isActive ->
                    if (selectedService == null) {
                        // Logika dodawania nowej usługi
                        tenantViewModel.addService(name, priceCents, duration)
                    } else {
                        // Logika edycji istniejącej usługi
                        tenantViewModel.updateService(
                            serviceId = selectedService!!.id,
                            name = name,
                            priceCents = priceCents,
                            duration = duration,
                            active = isActive
                        )
                    }
                    showAddEditDialog = false
                }
            )
        }
    }
}

@Composable
fun ServiceItemCard(
    service: ServiceCatalogItemDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (service.active) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (service.active) MaterialTheme.colorScheme.primaryContainer
                        else Color.LightGray
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = if (service.active) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (service.active) Color.Unspecified else Color.Gray
                )
                Text(
                    text = "${service.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val priceDisplay = service.priceCents / 100.0
                Text(
                    text = String.format("%.2f zł", priceDisplay),
                    fontWeight = FontWeight.ExtraBold,
                    color = if (service.active) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontSize = 16.sp
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edytuj", modifier = Modifier.size(20.dp), tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceAddEditDialog(
    service: ServiceCatalogItemDTO?,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(service?.name ?: "") }
    var price by remember { mutableStateOf(if(service != null) (service.priceCents / 100.0).toString() else "") }
    var duration by remember { mutableStateOf(service?.durationMinutes?.toString() ?: "") }
    var isActive by remember { mutableStateOf(service?.active ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (service == null) "Dodaj nową usługę" else "Edytuj usługę") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa zabiegu") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.replace(',', '.') },
                    label = { Text("Cena (zł)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Czas trwania (min)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Usługa aktywna")
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pCents = ((price.toDoubleOrNull() ?: 0.0) * 100).toInt()
                    val dMinutes = duration.toIntOrNull() ?: 0
                    onConfirm(name, pCents, dMinutes, isActive)
                },
                enabled = name.isNotBlank() && price.isNotBlank() && duration.isNotBlank()
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}