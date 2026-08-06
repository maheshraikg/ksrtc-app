package com.example.ksrtc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.model.FavoriteRouteEntity
import com.example.ksrtc.data.model.RecentSearchEntity
import com.example.ksrtc.data.model.StationEntity
import com.example.ksrtc.data.repository.KsrtcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: KsrtcRepository
) : ViewModel() {

    private val _fromStation = MutableStateFlow("Bengaluru Majestic")
    val fromStation: StateFlow<String> = _fromStation.asStateFlow()

    private val _toStation = MutableStateFlow("Mysuru Suburb")
    val toStation: StateFlow<String> = _toStation.asStateFlow()

    val allStations: StateFlow<List<StationEntity>> = repository.allStations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<RecentSearchEntity>> = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRoutes: StateFlow<List<FavoriteRouteEntity>> = repository.favoriteRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    fun setFromStation(station: String) {
        _fromStation.value = station
    }

    fun setToStation(station: String) {
        _toStation.value = station
    }

    fun swapStations() {
        val temp = _fromStation.value
        _fromStation.value = _toStation.value
        _toStation.value = temp
    }

    fun onRecentSearchClicked(recent: RecentSearchEntity) {
        if (recent.fromStation.isNotBlank()) _fromStation.value = recent.fromStation
        if (recent.toStation.isNotBlank()) _toStation.value = recent.toStation
    }

    fun selectPopularDestination(destination: String) {
        _toStation.value = destination
    }

    fun recordSearch() {
        viewModelScope.launch {
            repository.addRecentSearch(_fromStation.value, _toStation.value)
        }
    }
}
