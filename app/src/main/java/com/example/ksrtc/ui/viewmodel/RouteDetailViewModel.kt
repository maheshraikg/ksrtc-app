package com.example.ksrtc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.repository.KsrtcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteDetailViewModel(
    private val repository: KsrtcRepository
) : ViewModel() {

    private val _busDetail = MutableStateFlow<BusTimetableEntity?>(null)
    val busDetail: StateFlow<BusTimetableEntity?> = _busDetail.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    fun loadRouteDetail(id: Long) {
        viewModelScope.launch {
            val timetable = repository.getTimetableById(id)
            _busDetail.value = timetable
            timetable?.let {
                repository.isFavorite(it.id).collect { fav ->
                    _isFavorite.value = fav
                }
            }
        }
    }

    fun toggleFavorite() {
        val detail = _busDetail.value ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFavorite(detail.id)
            } else {
                repository.addFavorite(detail)
            }
        }
    }
}
