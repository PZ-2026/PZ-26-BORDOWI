package com.example.dentflow_android.data.remote

data class TenantResponse(
    val id: Long,
    val name: String,
    val status: String,
    val locations: List<LocationResponse>
)

data class LocationResponse(
    val id: Long,
    val tenantId: Long,
    val name: String,
    val addressStreet: String,
    val addressCity: String,
    val addressZip: String,
    val addressCountry: String
)

data class StaffMemberResponse(
    val id: Long,
    val tenantId: Long,
    val userId: Long,
    val displayName: String,
    val profession: String
)

data class CreatePatientRequest(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val notes: String
)