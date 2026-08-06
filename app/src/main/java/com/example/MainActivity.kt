package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ksrtc.data.local.AppDatabase
import com.example.ksrtc.data.local.PreferencesManager
import com.example.ksrtc.data.repository.KsrtcRepository
import com.example.ksrtc.ui.MainAppContainer
import com.example.ksrtc.ui.viewmodel.AdminViewModel
import com.example.ksrtc.ui.viewmodel.FavoritesViewModel
import com.example.ksrtc.ui.viewmodel.HomeViewModel
import com.example.ksrtc.ui.viewmodel.KsrtcViewModelFactory
import com.example.ksrtc.ui.viewmodel.RouteDetailViewModel
import com.example.ksrtc.ui.viewmodel.SearchViewModel
import com.example.ksrtc.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val prefs = PreferencesManager(applicationContext)
        val repository = KsrtcRepository(db.ksrtcDao(), prefs)

        val factory = KsrtcViewModelFactory(repository)

        val homeViewModel: HomeViewModel by viewModels { factory }
        val searchViewModel: SearchViewModel by viewModels { factory }
        val routeDetailViewModel: RouteDetailViewModel by viewModels { factory }
        val favoritesViewModel: FavoritesViewModel by viewModels { factory }
        val settingsViewModel: SettingsViewModel by viewModels { factory }
        val adminViewModel: AdminViewModel by viewModels { factory }

        setContent {
            MainAppContainer(
                repository = repository,
                homeViewModel = homeViewModel,
                searchViewModel = searchViewModel,
                routeDetailViewModel = routeDetailViewModel,
                favoritesViewModel = favoritesViewModel,
                settingsViewModel = settingsViewModel,
                adminViewModel = adminViewModel
            )
        }
    }
}
