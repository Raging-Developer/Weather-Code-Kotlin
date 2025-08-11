package com.app.new_weather

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel()  {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showSuccessScreen = MutableStateFlow(false)
    val showSuccessScreen: StateFlow<Boolean> = _showSuccessScreen.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun startedLoading() {
        _isLoading.value = true
        _showSuccessScreen.value = false // Reset success state
        _errorMessage.value = null      // Reset error state
    }

    fun onSuccess() {
        _isLoading.value = false
        _showSuccessScreen.value = true
        _errorMessage.value = null
    }

    fun onFailure(exception: Exception?) {
        _isLoading.value = false
        _showSuccessScreen.value = false // Ensure success screen isn't shown
        _errorMessage.value = "Ooops... could be a gateway problem, give it a swipe or rotate it and see what happens. ${exception?.message ?: ""}"
    }

    fun errorMessage() {
        _errorMessage.value = null
    }

    fun successScreen() {
        _showSuccessScreen.value = false
    }
}
