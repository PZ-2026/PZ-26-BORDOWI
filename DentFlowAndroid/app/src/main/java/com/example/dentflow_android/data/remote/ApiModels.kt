package com.example.dentflow_android.data.remote

data class VisitResponse(
    val id: Long,
    val patientId: Long,
    val visitDate: String,
    val description: String,
    val status: String
)

data class PatientResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phoneNumber: String? = null
)
