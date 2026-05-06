package com.example.dentflow_android.data.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _slots = MutableStateFlow<List<ScheduleSlotDTO>>(emptyList())
    val slots: StateFlow<List<ScheduleSlotDTO>> = _slots.asStateFlow()

    private val _blockers = MutableStateFlow<List<ScheduleBlockerDTO>>(emptyList())
    val blockers: StateFlow<List<ScheduleBlockerDTO>> = _blockers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- ŁADOWANIE DANYCH ---
    fun loadSchedule(tenantId: Long, userId: Long, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val now = OffsetDateTime.now()
                val fromStr = now.minusYears(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                val toStr = now.plusYears(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

                // Pobieranie slotów
                val slotsRes = apiService.getSlots(tenantId, fromStr, toStr)
                if (slotsRes.isSuccessful) {
                    val allSlots = slotsRes.body() ?: emptyList()
                    // Filtrowanie po roli, jeśli to lekarz (opcjonalnie, zależy od backendu)
                    _slots.value = if (role == "DENTIST") allSlots.filter { it.staffId == userId } else allSlots
                }

                // Pobieranie blokerów
                val blockersRes = apiService.getBlockers(tenantId)
                if (blockersRes.isSuccessful) {
                    val allBlockers = blockersRes.body() ?: emptyList()
                    _blockers.value = if (role == "DENTIST") allBlockers.filter { it.staffId == userId } else allBlockers
                }

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Błąd ładowania grafiku", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- OPERACJE NA SLOTACH ---
    fun addSlot(tenantId: Long, slot: ScheduleSlotDTO, role: String, userId: Long) {
        viewModelScope.launch {
            val request = CreateSlotRequest(
                staffId = slot.staffId,
                locationId = slot.locationId,
                roomId = slot.roomId,
                startAt = slot.startAt,
                endAt = slot.endAt
            )
            if (apiService.createSlot(tenantId, request).isSuccessful) {
                loadSchedule(tenantId, userId, role)
            }
        }
    }

    fun updateSlot(tenantId: Long, slotId: Long, slot: ScheduleSlotDTO, role: String, userId: Long) {
        viewModelScope.launch {
            val request = UpdateSlotRequest(
                locationId = slot.locationId,
                roomId = slot.roomId,
                startAt = slot.startAt,
                endAt = slot.endAt
            )
            if (apiService.updateSlot(tenantId, slotId, request).isSuccessful) {
                loadSchedule(tenantId, userId, role)
            }
        }
    }

    fun deleteSlot(tenantId: Long, slotId: Long, role: String, userId: Long) {
        viewModelScope.launch {
            if (apiService.deleteSlot(tenantId, slotId).isSuccessful) {
                loadSchedule(tenantId, userId, role)
            }
        }
    }

    // --- OPERACJE NA BLOKERACH (URLOPY/PRZERWY) ---
    fun addBlocker(tenantId: Long, blocker: ScheduleBlockerDTO, role: String, userId: Long) {
        viewModelScope.launch {
            val request = CreateBlockerRequest(
                staffId = blocker.staffId,
                roomId = blocker.roomId,
                startAt = blocker.startAt,
                endAt = blocker.endAt,
                reason = blocker.reason
            )
            if (apiService.createBlocker(tenantId, request).isSuccessful) {
                loadSchedule(tenantId, userId, role)
            }
        }
    }

    fun deleteBlocker(tenantId: Long, blockerId: Long, role: String, userId: Long) {
        viewModelScope.launch {
            if (apiService.deleteBlocker(tenantId, blockerId).isSuccessful) {
                loadSchedule(tenantId, userId, role)
            }
        }
    }
}