package com.example.energywizeapp.ui.screens.settingsView

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.energywizeapp.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    fun startService(context: Context) {
        // Implement code to start the service here
        _isServiceRunning.value = true
        println("hola ${_isServiceRunning.value}")

        // Start the service using an Intent
        val serviceIntent = Intent(context, NotificationService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    fun stopService(context: Context) {
        // Implement code to stop the service here
        _isServiceRunning.value = false
        println("hola ${_isServiceRunning.value}")

        // Stop the service using an Intent
        val serviceIntent = Intent(context, NotificationService::class.java)
        context.stopService(serviceIntent)
    }
}
