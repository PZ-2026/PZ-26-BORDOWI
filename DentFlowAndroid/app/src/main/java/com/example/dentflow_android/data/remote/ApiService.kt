package com.example.dentflow_android.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- WIZYTY ---
    // Zmieniamy nazwę na getVisits i dodajemy tenantId, żeby pasowało do ViewModelu
    @GET("tenants/{tenantId}/visits")
    suspend fun getVisits(
        @Path("tenantId") tenantId: Long
    ): Response<List<VisitResponse>>

    // Stara metoda (jeśli nadal jej używasz, zostawiam, ale ViewModel szuka tej z tenantId)
    @GET("api/visits")
    suspend fun getAllVisits(): Response<List<VisitResponse>>

    // --- PACJENCI ---
    // Poprawione: teraz przyjmuje tenantId i patientId, co naprawi błąd "Too many arguments"
    @GET("tenants/{tenantId}/patients/{patientId}")
    suspend fun getPatientById(
        @Path("tenantId") tenantId: Long,
        @Path("patientId") patientId: Long
    ): Response<PatientResponse>

    @GET("tenants/{tenantId}/patients")
    suspend fun getPatients(
        @Path("tenantId") tenantId: Long,
        @Query("search") search: String? = null
    ): Response<List<PatientResponse>>

    @POST("tenants/{tenantId}/patients")
    suspend fun addPatient(
        @Path("tenantId") tenantId: Long,
        @Body request: CreatePatientRequest
    ): Response<PatientResponse>

    @PUT("tenants/{tenantId}/patients/{patientId}")
    suspend fun updatePatient(
        @Path("tenantId") tenantId: Long,
        @Path("patientId") patientId: Long,
        @Body request: CreatePatientRequest
    ): Response<PatientResponse>

    // --- PERSONEL ---
    @GET("tenants/{tenantId}/staff")
    suspend fun getStaff(
        @Path("tenantId") tenantId: Long
    ): Response<List<StaffMemberResponse>>

    // --- GABINET ---
    @GET("tenants/{tenantId}")
    suspend fun getTenantDetails(
        @Path("tenantId") tenantId: Long
    ): Response<TenantResponse>
}