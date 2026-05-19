package com.example.dentflow_android.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- GABINET (TENANT) ---
    @POST("tenants")
    suspend fun createTenant(
        @Body request: TenantRequest
    ): Response<TenantResponse>

    @GET("tenants/{tenantId}")
    suspend fun getTenantDetails(
        @Path("tenantId") tenantId: Long
    ): Response<TenantResponse>

    @PUT("tenants/{tenantId}")
    suspend fun updateTenant(
        @Path("tenantId") tenantId: Long,
        @Body request: TenantRequest
    ): Response<TenantResponse>

    // --- PACJENCI ---
    @GET("tenants/{tenantId}/patients")
    suspend fun getPatients(
        @Path("tenantId") tenantId: Long,
        @Query("search") search: String? = null
    ): Response<List<PatientResponse>>

    @GET("tenants/{tenantId}/patients/{patientId}")
    suspend fun getPatientById(
        @Path("tenantId") tenantId: Long,
        @Path("patientId") patientId: Long
    ): Response<PatientResponse>

    @POST("tenants/{tenantId}/patients")
    suspend fun createPatient(
        @Path("tenantId") tenantId: Long,
        @Body request: CreatePatientRequest
    ): Response<PatientResponse>

    @PUT("tenants/{tenantId}/patients/{patientId}")
    suspend fun updatePatient(
        @Path("tenantId") tenantId: Long,
        @Path("patientId") patientId: Long,
        @Body request: CreatePatientRequest
    ): Response<PatientResponse>

    @DELETE("tenants/{tenantId}/patients/{patientId}")
    suspend fun deletePatient(
        @Path("tenantId") tenantId: Long,
        @Path("patientId") patientId: Long
    ): Response<Unit>

    // --- PERSONEL (STAFF) ---
    @GET("tenants/{tenantId}/staff")
    suspend fun getStaff(
        @Path("tenantId") tenantId: Long
    ): Response<List<StaffMemberResponse>>

    // --- WIZYTY ---
    @GET("tenants/{tenantId}/visits")
    suspend fun getVisits(
        @Path("tenantId") tenantId: Long
    ): Response<List<VisitResponse>>

    @GET("tenants/{tenantId}/appointments")
    suspend fun getAppointments(
        @Path("tenantId") tenantId: Long,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<List<AppointmentResponse>>

    @POST("tenants/{tenantId}/appointments")
    suspend fun createAppointment(
        @Path("tenantId") tenantId: Long,
        @Body request: CreateAppointmentRequest
    ): Response<AppointmentResponse>

    // --- CATALOG (USŁUGI) ---
    @GET("tenants/{tenantId}/catalog")
    suspend fun getServices(
        @Path("tenantId") tenantId: Long
    ): Response<List<ServiceCatalogItemDTO>>

    // --- SCHEDULE SLOTS (GRAFIK - TERMINY) ---
    @GET("tenants/{tenantId}/schedule/slots")
    suspend fun getSlots(
        @Path("tenantId") tenantId: Long,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<List<ScheduleSlotDTO>>

    @POST("tenants/{tenantId}/schedule/slots")
    suspend fun createSlot(
        @Path("tenantId") tenantId: Long,
        @Body request: CreateSlotRequest
    ): Response<ScheduleSlotDTO>

    @PUT("tenants/{tenantId}/schedule/slots/{slotId}")
    suspend fun updateSlot(
        @Path("tenantId") tenantId: Long,
        @Path("slotId") slotId: Long,
        @Body request: UpdateSlotRequest
    ): Response<ScheduleSlotDTO>

    @DELETE("tenants/{tenantId}/schedule/slots/{slotId}")
    suspend fun deleteSlot(
        @Path("tenantId") tenantId: Long,
        @Path("slotId") slotId: Long
    ): Response<Unit>

    // --- SCHEDULE BLOCKERS (PRZERWY / URLOPY) ---
    @GET("tenants/{tenantId}/schedule/blockers")
    suspend fun getBlockers(
        @Path("tenantId") tenantId: Long
    ): Response<List<ScheduleBlockerDTO>>

    @POST("tenants/{tenantId}/schedule/blockers")
    suspend fun createBlocker(
        @Path("tenantId") tenantId: Long,
        @Body request: CreateBlockerRequest
    ): Response<ScheduleBlockerDTO>

    @DELETE("tenants/{tenantId}/schedule/blockers/{blockerId}")
    suspend fun deleteBlocker(
        @Path("tenantId") tenantId: Long,
        @Path("blockerId") blockerId: Long
    ): Response<Unit>

    // --- STAFF (ALIAS) ---
    @GET("tenants/{tenantId}/staff")
    suspend fun getStaffMembers(
        @Path("tenantId") tenantId: Long
    ): Response<List<StaffMemberResponse>>
}
