package com.example.dentflow_android.data.ViewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.ApiService
import com.example.dentflow_android.data.remote.AppointmentResponse
import com.example.dentflow_android.data.remote.CreateAppointmentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointments: StateFlow<List<AppointmentResponse>> = _appointments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAppointments(date: LocalDate) {
        val tenantId = sharedPrefs.getLong("tenant_id", -1L)
        val currentUserId = sharedPrefs.getLong("user_id", -1L)
        val userRole = sharedPrefs.getString("user_role", "PATIENT")

        if (tenantId == -1L || currentUserId == -1L) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val startOfDay = "${date}T00:00:00Z"
                val endOfDay = "${date}T23:59:59Z"

                val response = apiService.getAppointments(tenantId, from = startOfDay, to = endOfDay)

                if (response.isSuccessful) {
                    val allVisits = response.body() ?: emptyList()
                    _appointments.value = if (userRole == "PATIENT") {
                        allVisits.filter { it.patientId == currentUserId }
                    } else {
                        allVisits
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAppointment(
        locId: Long,
        room: Long,
        docId: Long,
        patId: Long,
        servId: Long,
        start: String,
        end: String,
        note: String,
        onSuccess: () -> Unit
    ) {
        val tenantId = sharedPrefs.getLong("tenant_id", -1L)
        val userId = sharedPrefs.getLong("user_id", -1L)

        if (tenantId == -1L) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = CreateAppointmentRequest(
                    locationId = locId,
                    roomId = room,
                    dentistStaffId = docId,
                    patientId = patId,
                    serviceItemId = servId,
                    startAt = start,
                    endAt = end,
                    createdByUserId = userId,
                    notes = note
                )

                val response = apiService.createAppointment(tenantId, request)

                if (response.isSuccessful) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}