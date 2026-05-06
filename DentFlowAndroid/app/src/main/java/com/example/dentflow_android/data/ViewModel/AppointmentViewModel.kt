package com.example.dentflow_android.data.ViewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.ApiService
import com.example.dentflow_android.data.remote.AppointmentResponse
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

    /**
     * POPRAWIONA FUNKCJA:
     * Przyjmuje LocalDate (naprawia Argument type mismatch).
     * Pobiera tenantId z SharedPreferences (naprawia No value passed for tenantId).
     */
    fun fetchAppointments(date: LocalDate) {
        val tenantId = sharedPrefs.getLong("tenant_id", -1L)

        if (tenantId == -1L) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Wysyłamy tenantId oraz datę jako String (np. "2026-05-06")
                val response = apiService.getAppointments(tenantId, date.toString())

                if (response.isSuccessful) {
                    _appointments.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}