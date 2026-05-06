package com.example.dentflow_android.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- TENANTS (KLINIKI) ---
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

    // --- PATIENTS (PACJENCI) ---
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

    // --- STAFF (PERSONEL) ---
    @GET("tenants/{tenantId}/staff")
    suspend fun getStaffMembers(
        @Path("tenantId") tenantId: Long
    ): Response<List<StaffMemberResponse>>

    @POST("tenants/{tenantId}/staff")
    suspend fun createStaffMember(
        @Path("tenantId") tenantId: Long,
        @Body request: CreateStaffMemberRequest
    ): Response<StaffMemberResponse>

    @GET("tenants/{tenantId}/staff/{staffId}")
    suspend fun getStaffMemberById(
        @Path("tenantId") tenantId: Long,
        @Path("staffId") staffId: Long
    ): Response<StaffMemberResponse>

    @PUT("tenants/{tenantId}/staff/{staffId}")
    suspend fun updateStaffMember(
        @Path("tenantId") tenantId: Long,
        @Path("staffId") staffId: Long,
        @Body request: UpdateStaffMemberRequest
    ): Response<StaffMemberResponse>

    @DELETE("tenants/{tenantId}/staff/{staffId}")
    suspend fun deleteStaffMember(
        @Path("tenantId") tenantId: Long,
        @Path("staffId") staffId: Long
    ): Response<Unit>

    // --- VISITS & CATALOG ---
    @GET("tenants/{tenantId}/visits")
    suspend fun getVisits(
        @Path("tenantId") tenantId: Long
    ): Response<List<VisitResponse>>

    @GET("tenants/{tenantId}/catalog")
    suspend fun getServices(
        @Path("tenantId") tenantId: Long
    ): Response<List<ServiceCatalogItemDTO>>

    // --- SCHEDULE SLOTS (GRAFIK - WOLNE TERMINY) ---
    @GET("tenants/{tenantId}/schedule/slots")
    suspend fun getSlots(
        @Path("tenantId") tenantId: Long,
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<ScheduleSlotDTO>>

    @POST("tenants/{tenantId}/schedule/slots")
    suspend fun createSlot(
        @Path("tenantId") tenantId: Long,
        @Body request: CreateSlotRequest // Zmiana na CreateSlotRequest
    ): Response<ScheduleSlotDTO>

    @PUT("tenants/{tenantId}/schedule/slots/{slotId}")
    suspend fun updateSlot(
        @Path("tenantId") tenantId: Long,
        @Path("slotId") slotId: Long,
        @Body request: UpdateSlotRequest // Zmiana na UpdateSlotRequest
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

    @PUT("tenants/{tenantId}/schedule/blockers/{blockerId}")
    suspend fun updateBlocker(
        @Path("tenantId") tenantId: Long,
        @Path("blockerId") blockerId: Long,
        @Body request: UpdateBlockerRequest
    ): Response<ScheduleBlockerDTO>

    @DELETE("tenants/{tenantId}/schedule/blockers/{blockerId}")
    suspend fun deleteBlocker(
        @Path("tenantId") tenantId: Long,
        @Path("blockerId") blockerId: Long
    ): Response<Unit>

    @GET("tenants/{tenantId}/room")
    suspend fun getRooms(
        @Path("tenantId") tenantId: Long
    ): Response<List<RoomResponse>>

    @GET("tenants/{tenantId}/users/{userId}/notifications")
    suspend fun getNotifications(
        @Path("tenantId") tenantId: Long,
        @Path("userId") userId: Long,
        @Query("unreadOnly") unreadOnly: Boolean = false
    ): Response<List<NotificationDTO>>

    @POST("tenants/{tenantId}/users/{userId}/notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("tenantId") tenantId: Long,
        @Path("userId") userId: Long,
        @Path("notificationId") notificationId: Long
    ): Response<NotificationDTO>

    @GET("tenants/{tenantId}/users/{userId}/notifications/unread-count")
    suspend fun getUnreadCount(
        @Path("tenantId") tenantId: Long,
        @Path("userId") userId: Long
    ): Response<Int>

    @POST("tenants/{tenantId}/users/{userId}/notifications/read-all")
    suspend fun markAllNotificationsAsRead(
        @Path("tenantId") tenantId: Long,
        @Path("userId") userId: Long
    ): Response<Unit>
}