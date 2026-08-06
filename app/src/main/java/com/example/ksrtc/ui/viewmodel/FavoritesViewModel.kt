package com.example.ksrtc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.repository.KsrtcRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: KsrtcRepository
) : ViewModel() {

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val favoriteTimetables: StateFlow<List<BusTimetableEntity>> = combine(
        repository.favoriteRoutes,
        repository.allApprovedTimetables
    ) { favorites, timetables ->
        val favIds = favorites.map { it.timetableId }.toSet()
        timetables.filter { favIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFavorite(id: Long) {
        viewModelScope.launch {
            repository.removeFavorite(id)
        }
    }
}
