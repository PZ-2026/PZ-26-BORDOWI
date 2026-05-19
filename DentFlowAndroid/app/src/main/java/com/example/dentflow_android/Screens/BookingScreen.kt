package com.example.dentflow_android.Screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentflow_android.data.ViewModel.*
import com.example.dentflow_android.data.remote.*

/**
 * Ekran rezerwacji wizyty — wielokrokowy flow.
 * SCRUM-57: wybór lekarza → wybór usługi → wybór terminu → potwierdzenie
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    tenantId: Long = 1L,
    patientId: Long = 1L,
    locationId: Long = 1L,
    roomId: Long = 1L,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tenantId) {
        viewModel.loadInitialData(tenantId)
    }

    // Załaduj sloty przy przejściu do kroku wyboru terminu
    LaunchedEffect(uiState.step) {
        if (uiState.step == BookingStep.SelectDateTime) {
            viewModel.loadSlots(tenantId)
        }
    }

    val stepTitle = when (uiState.step) {
        BookingStep.SelectDentist  -> "Wybierz lekarza"
        BookingStep.SelectService  -> "Wybierz usługę"
        BookingStep.SelectDateTime -> "Wybierz termin"
        BookingStep.Confirm        -> "Potwierdź wizytę"
        BookingStep.Success        -> "Wizyta zarezerwowana"
    }

    val currentStepIndex = when (uiState.step) {
        BookingStep.SelectDentist  -> 0
        BookingStep.SelectService  -> 1
        BookingStep.SelectDateTime -> 2
        BookingStep.Confirm        -> 3
        BookingStep.Success        -> 4
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stepTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (uiState.step != BookingStep.Success) {
                        IconButton(onClick = {
                            if (uiState.step == BookingStep.SelectDentist) onBack()
                            else viewModel.goBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Pasek postępu (4 kroki)
            if (uiState.step != BookingStep.Success) {
                BookingProgressBar(currentStep = currentStepIndex, totalSteps = 4)
                Spacer(Modifier.height(8.dp))
            }

            // Błąd
            uiState.error?.let { err ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = err,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "booking_step"
            ) { step ->
                when (step) {
                    BookingStep.SelectDentist -> DentistSelectionStep(
                        dentists = uiState.dentists,
                        isLoading = uiState.isLoading,
                        onSelect = viewModel::selectDentist
                    )
                    BookingStep.SelectService -> ServiceSelectionStep(
                        services = uiState.services,
                        isLoading = uiState.isLoading,
                        onSelect = viewModel::selectService
                    )
                    BookingStep.SelectDateTime -> DateTimeSelectionStep(
                        slots = uiState.slots,
                        isLoading = uiState.isLoading,
                        onSelect = viewModel::selectSlot
                    )
                    BookingStep.Confirm -> ConfirmStep(
                        dentist = uiState.selectedDentist,
                        service = uiState.selectedService,
                        slot = uiState.selectedSlot,
                        isLoading = uiState.isLoading,
                        onConfirm = {
                            viewModel.confirmBooking(tenantId, patientId, locationId, roomId)
                        }
                    )
                    BookingStep.Success -> SuccessStep(onBack = {
                        viewModel.reset()
                        onBack()
                    })
                }
            }
        }
    }
}

@Composable
private fun BookingProgressBar(currentStep: Int, totalSteps: Int) {
    val labels = listOf("Lekarz", "Usługa", "Termin", "Potwierdzenie")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < currentStep) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            color = if (index <= currentStep) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = if (index <= currentStep) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (index < totalSteps - 1) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    color = if (index < currentStep) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DentistSelectionStep(
    dentists: List<StaffMemberResponse>,
    isLoading: Boolean,
    onSelect: (StaffMemberResponse) -> Unit
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (dentists.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("Brak dostępnych lekarzy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        items(dentists) { dentist ->
            SelectableCard(
                title = dentist.displayName,
                subtitle = dentist.profession,
                icon = Icons.Default.Person,
                onClick = { onSelect(dentist) }
            )
        }
    }
}

@Composable
private fun ServiceSelectionStep(
    services: List<ServiceCatalogItemDTO>,
    isLoading: Boolean,
    onSelect: (ServiceCatalogItemDTO) -> Unit
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (services.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("Brak dostępnych usług", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        items(services) { service ->
            SelectableCard(
                title = service.name,
                subtitle = "${service.durationMinutes} min • ${String.format("%.2f", service.priceCents / 100.0)} zł",
                icon = Icons.Default.MedicalServices,
                onClick = { onSelect(service) }
            )
        }
    }
}

@Composable
private fun DateTimeSelectionStep(
    slots: List<ScheduleSlotDTO>,
    isLoading: Boolean,
    onSelect: (ScheduleSlotDTO) -> Unit
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (slots.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Brak dostępnych terminów.\nSkontaktuj się z kliniką telefonicznie.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        items(slots) { slot ->
            val startFormatted = slot.startAt.replace("T", " ").take(16)
            val endFormatted = slot.endAt.replace("T", " ").takeLast(5)
            SelectableCard(
                title = startFormatted,
                subtitle = "Do $endFormatted",
                icon = Icons.Default.Schedule,
                onClick = { onSelect(slot) }
            )
        }
    }
}

@Composable
private fun ConfirmStep(
    dentist: StaffMemberResponse?,
    service: ServiceCatalogItemDTO?,
    slot: ScheduleSlotDTO?,
    isLoading: Boolean,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Sprawdź szczegóły wizyty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ConfirmRow(label = "Lekarz", value = dentist?.displayName ?: "—", icon = Icons.Default.Person)
                HorizontalDivider()
                ConfirmRow(label = "Usługa", value = service?.name ?: "—", icon = Icons.Default.MedicalServices)
                HorizontalDivider()
                ConfirmRow(
                    label = "Termin",
                    value = slot?.startAt?.replace("T", " ")?.take(16) ?: "—",
                    icon = Icons.Default.Event
                )
                if (service != null) {
                    HorizontalDivider()
                    ConfirmRow(
                        label = "Cena",
                        value = "${String.format("%.2f", service.priceCents / 100.0)} zł",
                        icon = Icons.Default.Payments
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Zarezerwuj wizytę", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ConfirmRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SuccessStep(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Wizyta zarezerwowana!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Otrzymasz powiadomienie z potwierdzeniem wizyty.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Powrót do głównego menu", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SelectableCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
