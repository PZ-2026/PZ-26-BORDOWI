package com.example.dentflow_android.data.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authService: AuthService
) : ViewModel() {

    private val _staffMembers = MutableStateFlow<List<StaffMemberResponse>>(emptyList())
    val staffMembers: StateFlow<List<StaffMemberResponse>> = _staffMembers

    private val _services = MutableStateFlow<List<ServiceCatalogItemDTO>>(emptyList())
    val services: StateFlow<List<ServiceCatalogItemDTO>> = _services

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- ŁADOWANIE DANYCH (Poprawione, aby nic nie pominąć) ---

    fun loadAllData(tenantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("DEBUG_DATA", "Rozpoczynam pobieranie dla tenantId: $tenantId")
            try {
                val staffDef = async { apiService.getStaffMembers(tenantId) }
                val servicesDef = async { apiService.getServices(tenantId) }

                val sRes = staffDef.await()
                val vRes = servicesDef.await()

                if (sRes.isSuccessful) {
                    _staffMembers.value = sRes.body() ?: emptyList()
                    Log.d("DEBUG_DATA", "Pobrano lekarzy: ${sRes.body()?.size}")
                } else {
                    Log.e("DEBUG_DATA", "Błąd lekarzy: ${sRes.code()} - ${sRes.errorBody()?.string()}")
                }

                if (vRes.isSuccessful) {
                    _services.value = vRes.body() ?: emptyList()
                    Log.d("DEBUG_DATA", "Pobrano usługi: ${vRes.body()?.size}")
                } else {
                    Log.e("DEBUG_DATA", "Błąd usług: ${vRes.code()} - ${vRes.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("DEBUG_DATA", "Wyjątek sieciowy: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStaff(tenantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getStaffMembers(tenantId)
                if (response.isSuccessful) {
                    _staffMembers.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("STAFF_VM", "Exception loadStaff: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    // --- ZARZĄDZANIE PERSONELEM (Twoje oryginalne funkcje) ---

    fun addStaff(fName: String, lName: String, profession: String, email: String, password: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val registerRequest = RegisterRequest(
                    email = email,
                    password = password,
                    firstName = fName,
                    lastName = lName,
                    phone = phone
                )

                val authResponse = authService.register(registerRequest)

                if (authResponse.isSuccessful && authResponse.body() != null) {
                    val createdUserId = authResponse.body()!!.userId
                    if (createdUserId != 0L) {
                        val staffRequest = CreateStaffMemberRequest(
                            userId = createdUserId,
                            displayName = "$fName $lName",
                            profession = profession
                        )
                        // Używamy tenantId = 1L zgodnie z Twoim oryginałem
                        val coreResponse = apiService.createStaffMember(1L, staffRequest)
                        if (coreResponse.isSuccessful) {
                            loadStaff(1L)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("STAFF_VM", "Exception addStaff: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStaff(tenantId: Long, staffId: Long, fName: String, lName: String, profession: String, userId: Long) {
        viewModelScope.launch {
            try {
                val updateRequest = UpdateStaffMemberRequest(
                    userId = userId,
                    displayName = "$fName $lName",
                    profession = profession
                )
                val response = apiService.updateStaffMember(tenantId, staffId, updateRequest)
                if (response.isSuccessful) {
                    loadStaff(tenantId)
                }
            } catch (e: Exception) {
                Log.e("STAFF_VM", "Exception updateStaff: ${e.message}")
            }
        }
    }

    fun deleteStaff(tenantId: Long, staffId: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteStaffMember(tenantId, staffId)
                if (response.isSuccessful) {
                    loadStaff(tenantId)
                }
            } catch (e: Exception) {
                Log.e("STAFF_VM", "Exception deleteStaff: ${e.message}")
            }
        }
    }
}