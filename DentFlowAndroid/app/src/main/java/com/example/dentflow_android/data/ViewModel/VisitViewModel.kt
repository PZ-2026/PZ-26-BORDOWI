package com.example.dentflow_android.data.ViewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitWithPatient(
    val visit: AppointmentResponse, // Zmienione z VisitResponse zgodnie z nowym ApiService
    val patient: PatientResponse?
)

@HiltViewModel
class VisitViewModel @Inject constructor(
    private val apiService: ApiService,
    private val prefs: SharedPreferences // Wstrzykujemy SharedPreferences
) : ViewModel() {

    private val _visits = MutableStateFlow<List<VisitWithPatient>>(emptyList())
    val visits: StateFlow<List<VisitWithPatient>> = _visits

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val TAG = "VISIT_VM_DEBUG"

    // Dynamiczne pobieranie tenantId z sesji
    private val currentTenantId: Long
        get() = prefs.getLong("tenant_id", -1L)

    init {
        refreshVisits()
    }

    fun refreshVisits() {
        val tenantId = currentTenantId
        if (tenantId == -1L) {
            Log.e(TAG, "Błąd: Brak tenantId w sesji. Nie można pobrać wizyt.")
            return
        }
        fetchVisitsWithPatients(tenantId)
    }

    private fun fetchVisitsWithPatients(tenantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Pobieranie wizyt dla kliniki: $tenantId")
            try {
                // Zmieniono na getAppointments() zgodnie z Twoim poprawionym ApiService
                val response = apiService.getAppointments(tenantId)

                if (response.isSuccessful) {
                    val appointmentList = response.body() ?: emptyList()
                    Log.d(TAG, "Pobrano ${appointmentList.size} wizyt. Rozpoczynam wiązanie z pacjentami...")

                    // OPTYMALIZACJA: Pobieramy dane pacjentów równolegle (async)
                    val combinedList = appointmentList.map { appointment ->
                        async {
                            val patientRes = apiService.getPatientById(tenantId, appointment.patientId)
                            VisitWithPatient(
                                visit = appointment,
                                patient = if (patientRes.isSuccessful) patientRes.body() else null
                            )
                        }
                    }.awaitAll()

                    _visits.value = combinedList
                    Log.d(TAG, "Dane wizyt i pacjentów połączone pomyślnie.")
                } else {
                    Log.e(TAG, "Błąd pobierania wizyt: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Wyjątek podczas pobierania wizyt: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Dodawanie nowej wizyty (przykład użycia dynamicznego ID)
    fun createAppointment(request: CreateAppointmentRequest) {
        val tenantId = currentTenantId
        if (tenantId == -1L) return

        viewModelScope.launch {
            try {
                val res = apiService.createAppointment(tenantId, request)
                if (res.isSuccessful) {
                    Log.d(TAG, "Wizyta utworzona pomyślnie.")
                    refreshVisits()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd tworzenia wizyty: ${e.message}")
            }
        }
    }
}