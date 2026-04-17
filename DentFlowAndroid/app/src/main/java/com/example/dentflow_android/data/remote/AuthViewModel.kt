package com.example.dentflow_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentflow_android.data.remote.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Zmieniona funkcja login:
     * - onSuccess przyjmuje teraz tylko Long (tenantId), bo rola nie przychodzi w JSONie.
     */
    fun login(request: LoginRequest, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = authService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    // Przekazujemy tylko tenantId, bo pola role nie ma w AuthResponse
                    onSuccess(body.tenantId)
                } else {
                    // Jeśli serwer zwróci 401 lub 403
                    _errorMessage.value = "Błędny e-mail lub hasło"
                }
            } catch (e: Exception) {
                // Jeśli np. serwer jest wyłączony lub adres IP jest złe
                _errorMessage.value = "Błąd połączenia z serwerem"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(request: RegisterRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = authService.register(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Rejestracja nieudana. Email może być zajęty."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd sieci: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}