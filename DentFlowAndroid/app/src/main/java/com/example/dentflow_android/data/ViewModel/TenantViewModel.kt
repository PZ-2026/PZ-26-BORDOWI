package com.example.dentflow_android.data.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TenantViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _tenantState = mutableStateOf<TenantResponse?>(null)
    val tenantState: State<TenantResponse?> = _tenantState

    private val _servicesState = mutableStateOf<List<ServiceCatalogItemDTO>>(emptyList())
    val servicesState: State<List<ServiceCatalogItemDTO>> = _servicesState

    // StateFlow dla pokoi, ponieważ użyliśmy .collectAsState() w widoku
    private val _rooms = MutableStateFlow<List<RoomResponse>>(emptyList())
    val rooms = _rooms.asStateFlow()

    private val TAG = "DENTFLOW_DEBUG"

    // Ładowanie wszystkich danych kliniki (wywoływane w MainActivity/BusinessScreen)
    fun loadAllTenantData(id: Long) {
        viewModelScope.launch {
            loadTenantData(id)
            loadServices(id)
            loadRooms(id) // Dodano ładowanie pokoi do ogólnej metody
        }
    }

    fun loadTenantData(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.getTenantDetails(id)
                if (response.isSuccessful) {
                    _tenantState.value = response.body()
                } else {
                    Log.e(TAG, "Błąd pobierania tenanta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Wyjątek sieciowy (tenant): ${e.message}")
            }
        }
    }

    fun loadServices(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.getServices(id)
                if (response.isSuccessful) {
                    _servicesState.value = response.body() ?: emptyList()
                    Log.d(TAG, "Pobrano usługi: ${_servicesState.value.size}")
                } else {
                    Log.e(TAG, "Błąd pobierania usług: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Wyjątek usługi: ${e.message}")
            }
        }
    }

    // Pobieranie listy pokoi z API
    fun loadRooms(tenantId: Long) {
        viewModelScope.launch {
            try {
                val res = apiService.getRooms(tenantId)
                if (res.isSuccessful) {
                    _rooms.value = res.body() ?: emptyList()
                    Log.d(TAG, "Pobrano pokoje: ${_rooms.value.size}")
                } else {
                    Log.e(TAG, "Błąd pobierania pokoi: ${res.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Wyjątek pokoje: ${e.message}")
            }
        }
    }

    fun saveBusinessData(name: String, locName: String, street: String, city: String, zip: String) {
        viewModelScope.launch {
            val request = TenantRequest(
                name = name,
                location = LocationRequest(
                    name = locName,
                    addressStreet = street,
                    addressCity = city,
                    addressZip = zip,
                    addressCountry = "Polska"
                )
            )
            try {
                val currentId = _tenantState.value?.id ?: 1L
                val response = apiService.updateTenant(currentId, request)
                if (response.isSuccessful) {
                    _tenantState.value = response.body()
                    // Po aktualizacji danych kliniki warto przeładować listę,
                    // bo adresy lub nazwy lokalizacji mogły się zmienić
                    loadRooms(currentId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd zapisu danych firmy: ${e.message}")
            }
        }
    }
}