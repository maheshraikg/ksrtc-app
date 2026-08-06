package com.example.ksrtc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.repository.KsrtcRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: KsrtcRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBusType = MutableStateFlow("All")
    val selectedBusType: StateFlow<String> = _selectedBusType.asStateFlow()

    private val _sortBy = MutableStateFlow("Departure Time") // "Departure Time", "Bus Type", "Duration"
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<BusTimetableEntity>> = combine(
        _searchQuery,
        _selectedBusType,
        _sortBy
    ) { query, typeFilter, sort ->
        Triple(query, typeFilter, sort)
    }.flatMapLatest { (query, typeFilter, sort) ->
        repository.searchTimetables(query)
    }.combine(_selectedBusType) { list, busType ->
        if (busType == "All") list
        else list.filter { it.busType.equals(busType, ignoreCase = true) || (busType == "Volvo" && (it.busType.contains("Airavat", true) || it.busType.contains("Ambari", true))) }
    }.combine(_sortBy) { list, sort ->
        when (sort) {
            "Departure Time" -> list.sortedBy { parseTimeInMinutes(it.departureTime) }
            "Bus Type" -> list.sortedBy { it.busType }
            "Duration" -> list.sortedBy { parseDurationInMinutes(it.journeyDuration) }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectBusTypeFilter(busType: String) {
        _selectedBusType.value = busType
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun toggleFavorite(timetable: BusTimetableEntity, isFav: Boolean) {
        viewModelScope.launch {
            if (isFav) {
                repository.removeFavorite(timetable.id)
            } else {
                repository.addFavorite(timetable)
            }
        }
    }

    private fun parseTimeInMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.trim().split(" ")
            val timeParts = parts[0].split(":")
            var hour = timeParts[0].toInt()
            val min = timeParts[1].toInt()
            val isPm = parts.getOrNull(1)?.uppercase() == "PM"
            if (isPm && hour < 12) hour += 12
            if (!isPm && hour == 12) hour = 0
            hour * 60 + min
        } catch (e: Exception) {
            0
        }
    }

    private fun parseDurationInMinutes(durationStr: String): Int {
        return try {
            val hMatch = Regex("(\\d+)h").find(durationStr)?.groupValues?.get(1)?.toInt() ?: 0
            val mMatch = Regex("(\\d+)m").find(durationStr)?.groupValues?.get(1)?.toInt() ?: 0
            hMatch * 60 + mMatch
        } catch (e: Exception) {
            0
        }
    }
}
