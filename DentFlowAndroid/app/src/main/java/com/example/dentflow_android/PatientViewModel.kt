package com.example.dentflow_android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
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

    // Nowy stan, który informuje widok o trwającym pobieraniu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPatients(tenantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true // Zaczynamy ładowanie
            try {
                val response = apiService.getPatients(tenantId)
                if (response.isSuccessful) {
                    _patients.value = response.body() ?: emptyList()
                } else {
                    // Tutaj w przyszłości możesz obsłużyć błędy serwera (np. 404, 500)
                    println("Błąd API: ${response.code()}")
                }
            } catch (e: Exception) {
                // To wyłapie błędy sieciowe (np. wyłączony backend)
                e.printStackTrace()
            } finally {
                // Wykona się ZAWSZE na końcu - kółko przestanie się kręcić
                _isLoading.value = false
            }
        }
    }
}