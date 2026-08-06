package com.example.ksrtc.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ksrtc_app_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(getSavedLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(getSavedThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(prefs.getBoolean(KEY_IS_ADMIN, false))
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.name).apply()
        _language.value = lang
    }

    private fun getSavedLanguage(): AppLanguage {
        val name = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.name)
        return try {
            AppLanguage.valueOf(name ?: AppLanguage.ENGLISH.name)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    private fun getSavedThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.LIGHT.name)
        } catch (e: Exception) {
            AppThemeMode.LIGHT
        }
    }

    fun setAdminLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ADMIN, loggedIn).apply()
        _isAdminLoggedIn.value = loggedIn
    }

    fun clearCache() {
        prefs.edit().remove("last_cache_time").apply()
    }

    companion object {
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_THEME_MODE = "app_theme_mode"
        private const val KEY_IS_ADMIN = "is_admin_logged_in"
    }
}
