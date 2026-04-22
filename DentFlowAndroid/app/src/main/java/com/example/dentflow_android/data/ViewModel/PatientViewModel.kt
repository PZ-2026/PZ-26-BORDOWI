package com.example.dentflow_android.data.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.ApiService
import com.example.dentflow_android.data.remote.CreatePatientRequest
import com.example.dentflow_android.data.remote.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _patients = MutableStateFlow<List<PatientResponse>>(emptyList())
    val patients: StateFlow<List<PatientResponse>> = _patients

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val TAG = "PATIENT_VM_DEBUG"

<<<<<<< HEAD
    // Stała dla tenantId - docelowo powinna pochodzić z sesji użytkownika
    private val currentTenantId = 1L

    // 1. POBIERANIE PACJENTÓW
=======
    private val currentTenantId = 1L

>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
    fun fetchPatients(tenantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPatients(tenantId)
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _patients.value = list

<<<<<<< HEAD
                    // Logowanie, aby sprawdzić czy telefon przychodzi z bazy
=======
>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
                    list.forEach {
                        Log.d(TAG, "Pacjent: ${it.firstName}, Telefon: ${it.phone}")
                    }
                } else {
                    Log.e(TAG, "Błąd pobierania: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Wyjątek sieciowy: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

<<<<<<< HEAD
    // Alias dla kompatybilności wstecznej
    fun loadPatients(tenantId: Long) = fetchPatients(tenantId)

    // 2. DODAWANIE PACJENTA
=======
    fun loadPatients(tenantId: Long) = fetchPatients(tenantId)

>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
    fun addPatient(firstName: String, lastName: String, email: String, phone: String) {
        viewModelScope.launch {
            try {
                val request = CreatePatientRequest(
                    firstName = firstName,
                    lastName = lastName,
<<<<<<< HEAD
                    phone = phone, // Kluczowe pole
=======
                    phone = phone,
>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
                    email = email,
                    notes = ""
                )

                val response = apiService.createPatient(currentTenantId, request)
                if (response.isSuccessful) {
                    Log.d(TAG, "Dodano pacjenta pomyślnie")
                    fetchPatients(currentTenantId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd dodawania: ${e.message}")
            }
        }
    }

<<<<<<< HEAD
    // 3. EDYCJA PACJENTA
=======
>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
    fun updatePatient(id: Long, firstName: String, lastName: String, email: String, phone: String) {
        viewModelScope.launch {
            try {
                val request = CreatePatientRequest(
                    firstName = firstName,
                    lastName = lastName,
<<<<<<< HEAD
                    phone = phone, // Przesyłamy edytowany telefon
=======
                    phone = phone,
>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
                    email = email,
                    notes = ""
                )

                val response = apiService.updatePatient(currentTenantId, id, request)
                if (response.isSuccessful) {
                    Log.d(TAG, "Zaktualizowano pacjenta id: $id")
                    fetchPatients(currentTenantId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd aktualizacji: ${e.message}")
            }
        }
    }
<<<<<<< HEAD

    // 4. USUWANIE PACJENTA
=======
    
>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
    fun deletePatient(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.deletePatient(currentTenantId, id)
                if (response.isSuccessful) {
                    _patients.value = _patients.value.filter { it.id != id }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd usuwania: ${e.message}")
            }
        }
    }
}
