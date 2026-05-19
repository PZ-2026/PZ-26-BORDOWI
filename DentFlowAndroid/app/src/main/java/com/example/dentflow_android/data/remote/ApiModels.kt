package com.example.dentflow_android.data.remote

import com.google.gson.annotations.SerializedName

// --- GABINET (TENANT) ---
data class TenantResponse(
    val id: Long,
    val name: String,
    val status: String? = null,
    val locations: List<LocationResponse>? = emptyList()
)

data class LocationResponse(
    val id: Long,
    val tenantId: Long,
    val name: String,
    // Korzystamy z camelCase dokładnie tak, jak zwraca Twój backend
    val addressStreet: String,
    val addressCity: String,
    val addressZip: String,
    val addressCountry: String
)

// --- PACJENCI ---
data class PatientResponse(
    val id: Long,
    val tenantId: Long,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String,
    val notes: String? = null
)

// --- PERSONEL ---
data class StaffMemberResponse(
    val id: Long,
    val tenantId: Long,
    val userId: Long,
    val displayName: String,
    val profession: String
)

// --- WIZYTY ---
data class VisitResponse(
    val id: Long,
    val tenantId: Long,
    val patientId: Long,
    val staffId: Long? = null,
    val startTime: String,
    val endTime: String? = null,
    val description: String? = null,
    val status: String
)

// --- REQUESTS (Wysyłanie danych do bazy) ---
data class LocationRequest(
    val name: String,
    val addressStreet: String,
    val addressCity: String,
    val addressZip: String,
    val addressCountry: String = "Polska"
)

data class TenantRequest(
    val name: String,
    val location: LocationRequest
)

data class CreatePatientRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val notes: String = ""
)

// --- APPOINTMENTS (Wizyty) ---

data class AppointmentResponse(
    val id: Long,
    val tenantId: Long,
    val locationId: Long,
    val roomId: Long,
    val dentistStaffId: Long,
    val patientId: Long,
    val serviceItemId: Long,
    val startAt: String, // Zmienione z startTime zgodnie z API
    val endAt: String,   // Zmienione z endTime zgodnie z API
    val status: String,
    val notes: String? = null,
    val createdByUserId: Long? = null
)

data class CreateAppointmentRequest(
    val locationId: Long,
    val roomId: Long,
    val dentistStaffId: Long,
    val patientId: Long,
    val serviceItemId: Long,
    val startAt: String,
    val endAt: String,
    val createdByUserId: Long,
    val notes: String = ""
)

// --- CATALOG ---
data class ServiceCatalogItemDTO(
    val id: Long,
    val tenantId: Long,
    val name: String,
    val durationMinutes: Int,
    val priceCents: Long,
    val active: Boolean
)

// --- SCHEDULING (Sloty i Blokady) ---

data class ScheduleSlotDTO(
    val id: Long,
    val tenantId: Long,
    val staffId: Long,
    val locationId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String
)

data class CreateSlotRequest(
    val staffId: Long,
    val locationId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String
)

data class ScheduleBlockerDTO(
    val id: Long,
    val tenantId: Long,
    val staffId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String,
    val reason: String
)

data class CreateBlockerRequest(
    val staffId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String,
    val reason: String
)

data class UpdateSlotRequest(
    val locationId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String
)
