package com.example.ksrtc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksrtc.data.model.AdminSubmissionEntity
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.repository.KsrtcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: KsrtcRepository
) : ViewModel() {

    val isAdminLoggedIn: StateFlow<Boolean> = repository.isAdminLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val allTimetables: StateFlow<List<BusTimetableEntity>> = repository.allTimetablesAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCount: StateFlow<Int> = repository.activeCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminSubmissions: StateFlow<List<AdminSubmissionEntity>> = repository.adminSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _draftExtractedRows = MutableStateFlow<List<BusTimetableEntity>>(emptyList())
    val draftExtractedRows: StateFlow<List<BusTimetableEntity>> = _draftExtractedRows.asStateFlow()

    private val _isExtractingPdf = MutableStateFlow(false)
    val isExtractingPdf: StateFlow<Boolean> = _isExtractingPdf.asStateFlow()

    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage: StateFlow<String?> = _adminMessage.asStateFlow()

    fun loginAdmin(pin: String): Boolean {
        return if (pin.trim() == "admin123" || pin.trim() == "1234" || pin.trim().equals("admin", true)) {
            repository.setAdminLoggedIn(true)
            _adminMessage.value = "Admin session authenticated successfully."
            true
        } else {
            _adminMessage.value = "Invalid Admin Password/PIN. Try 'admin123'"
            false
        }
    }

    fun logoutAdmin() {
        repository.setAdminLoggedIn(false)
        _adminMessage.value = "Logged out from Admin mode."
    }

    fun simulatePdfUpload(fileName: String, customText: String? = null) {
        viewModelScope.launch {
            _isExtractingPdf.value = true
            val extracted = repository.extractFromPdf(fileName, customText)
            _draftExtractedRows.value = extracted
            _isExtractingPdf.value = false
            _adminMessage.value = "Successfully extracted ${extracted.size} timetable records from $fileName."
        }
    }

    fun updateDraftRow(index: Int, updatedItem: BusTimetableEntity) {
        val current = _draftExtractedRows.value.toMutableList()
        if (index in current.indices) {
            current[index] = updatedItem
            _draftExtractedRows.value = current
        }
    }

    fun removeDraftRow(index: Int) {
        val current = _draftExtractedRows.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _draftExtractedRows.value = current
        }
    }

    fun approveDraftToDatabase() {
        viewModelScope.launch {
            val drafts = _draftExtractedRows.value
            if (drafts.isEmpty()) return@launch
            drafts.forEach { item ->
                repository.insertTimetable(item.copy(status = "APPROVED"))
            }
            _draftExtractedRows.value = emptyList()
            _adminMessage.value = "Approved and saved ${drafts.size} timetables to the database."
        }
    }

    fun clearDrafts() {
        _draftExtractedRows.value = emptyList()
    }

    fun saveNewTimetable(timetable: BusTimetableEntity) {
        viewModelScope.launch {
            repository.insertTimetable(timetable)
            _adminMessage.value = "New route timetable added successfully."
        }
    }

    fun updateTimetable(timetable: BusTimetableEntity) {
        viewModelScope.launch {
            repository.updateTimetable(timetable)
            _adminMessage.value = "Route ${timetable.busNumber} updated."
        }
    }

    fun deleteTimetable(id: Long) {
        viewModelScope.launch {
            repository.deleteTimetable(id)
            _adminMessage.value = "Timetable entry deleted."
        }
    }

    fun approveTimetable(id: Long) {
        viewModelScope.launch {
            repository.approveTimetable(id)
            _adminMessage.value = "Status updated to APPROVED."
        }
    }

    fun rejectTimetable(id: Long) {
        viewModelScope.launch {
            repository.rejectTimetable(id)
            _adminMessage.value = "Status updated to REJECTED."
        }
    }

    fun generateCsvExport(): String {
        val list = allTimetables.value
        val sb = StringBuilder()
        sb.append("BusNumber,BusName,BusType,From,To,Via,Departure,Arrival,Duration,Depot,Status\n")
        list.forEach {
            sb.append("${it.busNumber},\"${it.busName}\",${it.busType},\"${it.fromStation}\",\"${it.toStation}\",\"${it.viaStops}\",${it.departureTime},${it.arrivalTime},${it.journeyDuration},\"${it.depot}\",${it.status}\n")
        }
        return sb.toString()
    }

    fun clearMessage() {
        _adminMessage.value = null
    }
}
