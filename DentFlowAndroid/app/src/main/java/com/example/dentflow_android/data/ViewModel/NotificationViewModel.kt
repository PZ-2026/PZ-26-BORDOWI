package com.example.dentflow_android.data.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationDTO>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val TAG = "NotificationVM"

    // Pobieranie pełnej listy powiadomień
    fun fetchNotifications(tenantId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                val res = apiService.getNotifications(tenantId, userId)
                if (res.isSuccessful) {
                    _notifications.value = res.body() ?: emptyList()
                    // Po pobraniu listy warto od razu odświeżyć licznik nieprzeczytanych
                    updateUnreadCount(tenantId, userId)
                } else {
                    Log.e(TAG, "Błąd pobierania powiadomień: ${res.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Wyjątek sieciowy: ${e.message}")
            }
        }
    }

    // Aktualizacja samego licznika (wywoływana wewnętrznie lub np. przez polling)
    fun updateUnreadCount(tenantId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                val res = apiService.getUnreadCount(tenantId, userId)
                if (res.isSuccessful) {
                    _unreadCount.value = res.body() ?: 0
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd licznika: ${e.message}")
            }
        }
    }

    // Oznaczanie pojedynczego powiadomienia jako przeczytane
    fun markRead(tenantId: Long, userId: Long, notificationId: Long) {
        viewModelScope.launch {
            try {
                val res = apiService.markAsRead(tenantId, userId, notificationId)
                if (res.isSuccessful) {
                    // Odświeżamy dane, aby UI zareagował na zmianę stanu 'read'
                    fetchNotifications(tenantId, userId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd oznaczania powiadomienia: ${e.message}")
            }
        }
    }

    // --- DODANA METODA: Oznaczanie wszystkich jako przeczytane ---
    fun markAllAsRead(tenantId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                val res = apiService.markAllNotificationsAsRead(tenantId, userId)
                if (res.isSuccessful) {
                    // Po sukcesie przeładowujemy listę i licznik
                    fetchNotifications(tenantId, userId)
                } else {
                    Log.e(TAG, "Błąd oznaczania wszystkich: ${res.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Błąd markAllAsRead: ${e.message}")
            }
        }
    }
}