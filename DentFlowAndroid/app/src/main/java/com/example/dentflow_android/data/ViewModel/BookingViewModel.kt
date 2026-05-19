package com.example.dentflow_android.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingStep {
    object SelectDentist : BookingStep()
    object SelectService : BookingStep()
    object SelectDateTime : BookingStep()
    object Confirm : BookingStep()
    object Success : BookingStep()
}

data class BookingUiState(
    val step: BookingStep = BookingStep.SelectDentist,
    val dentists: List<StaffMemberResponse> = emptyList(),
    val services: List<ServiceCatalogItemDTO> = emptyList(),
    val slots: List<ScheduleSlotDTO> = emptyList(),
    val selectedDentist: StaffMemberResponse? = null,
    val selectedService: ServiceCatalogItemDTO? = null,
    val selectedSlot: ScheduleSlotDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val bookingSuccess: Boolean = false
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState

    fun loadInitialData(tenantId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val staffResp = apiService.getStaffMembers(tenantId)
                val catalogResp = apiService.getServices(tenantId)
                _uiState.value = _uiState.value.copy(
                    dentists = if (staffResp.isSuccessful) staffResp.body() ?: emptyList() else emptyList(),
                    services = if (catalogResp.isSuccessful) catalogResp.body() ?: emptyList() else emptyList(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Błąd ładowania danych: ${e.localizedMessage}"
                )
            }
        }
    }

    fun selectDentist(dentist: StaffMemberResponse) {
        _uiState.value = _uiState.value.copy(
            selectedDentist = dentist,
            step = BookingStep.SelectService
        )
    }

    fun selectService(service: ServiceCatalogItemDTO) {
        _uiState.value = _uiState.value.copy(
            selectedService = service,
            step = BookingStep.SelectDateTime
        )
    }

    fun loadSlots(tenantId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val resp = apiService.getSlots(tenantId)
                _uiState.value = _uiState.value.copy(
                    slots = if (resp.isSuccessful) resp.body() ?: emptyList() else emptyList(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Błąd ładowania terminów: ${e.localizedMessage}"
                )
            }
        }
    }

    fun selectSlot(slot: ScheduleSlotDTO) {
        _uiState.value = _uiState.value.copy(
            selectedSlot = slot,
            step = BookingStep.Confirm
        )
    }

    fun confirmBooking(tenantId: Long, patientId: Long, locationId: Long, roomId: Long) {
        val state = _uiState.value
        val dentist = state.selectedDentist ?: return
        val service = state.selectedService ?: return
        val slot = state.selectedSlot ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val request = CreateAppointmentRequest(
                    locationId = locationId,
                    roomId = roomId,
                    dentistStaffId = dentist.id,
                    patientId = patientId,
                    serviceItemId = service.id,
                    startAt = slot.startAt,
                    endAt = slot.endAt,
                    createdByUserId = patientId,
                    notes = ""
                )
                val resp = apiService.createAppointment(tenantId, request)
                if (resp.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookingSuccess = true,
                        step = BookingStep.Success
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Błąd rezerwacji (${resp.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Błąd sieciowy: ${e.localizedMessage}"
                )
            }
        }
    }

    fun goBack() {
        val prevStep = when (_uiState.value.step) {
            BookingStep.SelectService -> BookingStep.SelectDentist
            BookingStep.SelectDateTime -> BookingStep.SelectService
            BookingStep.Confirm -> BookingStep.SelectDateTime
            else -> null
        }
        prevStep?.let { _uiState.value = _uiState.value.copy(step = it) }
    }

    fun reset() {
        _uiState.value = BookingUiState()
    }
}
