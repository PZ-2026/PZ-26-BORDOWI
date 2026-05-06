package com.example.dentflow_android.data.remote

import com.google.gson.annotations.SerializedName

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
    val addressStreet: String,
    val addressCity: String,
    val addressZip: String,
    val addressCountry: String,
    val phone: String? = null
)

data class PatientResponse(
    val id: Long,
    val tenantId: Long,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String,
    val notes: String? = null
)

data class StaffMemberResponse(
    val id: Long,
    val tenantId: Long,
    val userId: Long,
    val displayName: String,
    val profession: String
)

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

data class ServiceCatalogItemDTO(
    val id: Long,
    @SerializedName("tenant_id")
    val tenantId: Long,

    val name: String,

    @SerializedName("duration_minutes")
    val durationMinutes: Int,

    @SerializedName("price_cents")
    val priceCents: Long,

    val active: Boolean
)
data class LocationRequest(
    val name: String,
    val addressStreet: String,
    val addressCity: String,
    val addressZip: String,
    val addressCountry: String = "Polska",
    val phone: String = ""
)

data class TenantRequest(
    val name: String,
    val location: LocationRequest
)
data class CreateSlotRequest(
    val staffId: Long,
    val locationId: Long,
    val roomId: Long,
    val startAt: String, // format ISO
    val endAt: String
)

// Żądanie aktualizacji istniejącego slotu
data class UpdateSlotRequest(
    val locationId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String
)
// Żądanie utworzenia blokady
data class CreateBlockerRequest(
    val staffId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String,
    val reason: String
)

// Żądanie aktualizacji blokady
data class UpdateBlockerRequest(
    val roomId: Long,
    val startAt: String,
    val endAt: String,
    val reason: String
)

data class CreateVisitRequest(
    val patientId: Long,
    val staffId: Long,
    val slotId: Long? = null, // Jeśli wizyta jest przypięta do konkretnego slotu
    val startTime: String,
    val endTime: String,
    val description: String? = null
)

// Slot - dostępny termin
data class ScheduleSlotDTO(
    val id: Long,
    val tenantId: Long,
    val staffId: Long,
    val locationId: Long,
    val roomId: Long,
    val startAt: String, // ISO Date-time
    val endAt: String
)

// Blocker - przerwa/urlop
data class ScheduleBlockerDTO(
    val id: Long,
    val tenantId: Long,
    val staffId: Long,
    val roomId: Long,
    val startAt: String,
    val endAt: String,
    val reason: String
)

data class RoomResponse(
    val id: Long,
    val tenantId: Long,
    val locationId: Long,
    val name: String
)

data class CreatePatientRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val notes: String = ""
)

data class CreateStaffMemberRequest(
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("profession")
    val profession: String
)

data class UpdateStaffMemberRequest(
    val userId: Long,
    val displayName: String,
    val profession: String
)

data class NotificationDTO(
    val id: Long,
    val tenantId: Long,
    val userId: Long,
    val type: String,
    val message: String,
    val read: Boolean,
    val createdAt: String
)

data class CreateNotificationRequest(
    val userId: Long,
    val type: String,
    val message: String
)