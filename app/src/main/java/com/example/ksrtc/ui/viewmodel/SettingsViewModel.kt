package com.example.ksrtc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.AppThemeMode
import com.example.ksrtc.data.repository.KsrtcRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: KsrtcRepository
) : ViewModel() {

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val currentThemeMode: StateFlow<AppThemeMode> = repository.currentThemeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.SYSTEM)

    val isAdminLoggedIn: StateFlow<Boolean> = repository.isAdminLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setLanguage(language: AppLanguage) {
        repository.setLanguage(language)
    }

    fun setThemeMode(themeMode: AppThemeMode) {
        repository.setThemeMode(themeMode)
    }

    fun logoutAdmin() {
        repository.setAdminLoggedIn(false)
    }

    fun clearCache() {
        repository.clearCache()
    }
}
