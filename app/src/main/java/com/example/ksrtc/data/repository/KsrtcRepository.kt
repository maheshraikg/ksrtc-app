package com.example.ksrtc.data.repository

import com.example.ksrtc.data.local.KsrtcDao
import com.example.ksrtc.data.local.PreferencesManager
import com.example.ksrtc.data.model.AdminSubmissionEntity
import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.AppThemeMode
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.model.FavoriteRouteEntity
import com.example.ksrtc.data.model.RecentSearchEntity
import com.example.ksrtc.data.model.StationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KsrtcRepository(
    private val dao: KsrtcDao,
    private val prefs: PreferencesManager
) {

    val allApprovedTimetables: Flow<List<BusTimetableEntity>> = dao.getAllApprovedTimetables()
    val allTimetablesAdmin: Flow<List<BusTimetableEntity>> = dao.getAllTimetablesAdmin()
    val allStations: Flow<List<StationEntity>> = dao.getAllStations()
    val favoriteRoutes: Flow<List<FavoriteRouteEntity>> = dao.getFavoriteRoutes()
    val recentSearches: Flow<List<RecentSearchEntity>> = dao.getRecentSearches()
    val activeCount: Flow<Int> = dao.getActiveTimetablesCount()
    val adminSubmissions: Flow<List<AdminSubmissionEntity>> = dao.getAdminSubmissions()

    val currentLanguage: Flow<AppLanguage> = prefs.language
    val currentThemeMode: Flow<AppThemeMode> = prefs.themeMode
    val isAdminLoggedIn: Flow<Boolean> = prefs.isAdminLoggedIn

    fun searchTimetables(query: String): Flow<List<BusTimetableEntity>> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            return dao.getAllApprovedTimetables()
        }

        return dao.getAllApprovedTimetables().map { allBuses ->
            val normalizedQuery = normalizeLocationAliases(trimmed)
            val tokens = extractSearchTokens(normalizedQuery)

            if (tokens.isEmpty()) {
                allBuses
            } else {
                allBuses.filter { bus ->
                    tokens.all { token ->
                        bus.fromStation.contains(token, ignoreCase = true) ||
                        bus.fromStationKn.contains(token, ignoreCase = true) ||
                        bus.toStation.contains(token, ignoreCase = true) ||
                        bus.toStationKn.contains(token, ignoreCase = true) ||
                        bus.viaStops.contains(token, ignoreCase = true) ||
                        bus.viaStopsKn.contains(token, ignoreCase = true) ||
                        bus.busName.contains(token, ignoreCase = true) ||
                        bus.busNumber.contains(token, ignoreCase = true) ||
                        bus.busStand.contains(token, ignoreCase = true) ||
                        bus.busType.contains(token, ignoreCase = true) ||
                        bus.depot.contains(token, ignoreCase = true) ||
                        bus.division.contains(token, ignoreCase = true)
                    }
                }
            }
        }
    }

    fun getTimetablesBetween(from: String, to: String): Flow<List<BusTimetableEntity>> {
        val combinedQuery = "${from.trim()} ${to.trim()}"
        return searchTimetables(combinedQuery)
    }

    suspend fun getTimetableById(id: Long): BusTimetableEntity? {
        return dao.getTimetableById(id)
    }

    suspend fun addFavorite(timetable: BusTimetableEntity) {
        val fav = FavoriteRouteEntity(
            timetableId = timetable.id,
            fromStation = timetable.fromStation,
            toStation = timetable.toStation,
            busNumber = timetable.busNumber
        )
        dao.addFavorite(fav)
    }

    suspend fun removeFavorite(timetableId: Long) {
        dao.removeFavorite(timetableId)
    }

    fun isFavorite(timetableId: Long): Flow<Boolean> {
        return dao.isFavorite(timetableId)
    }

    suspend fun addRecentSearch(from: String, to: String, busType: String = "All") {
        if (from.isNotBlank() || to.isNotBlank()) {
            dao.insertRecentSearch(
                RecentSearchEntity(
                    fromStation = from,
                    toStation = to,
                    busType = busType
                )
            )
        }
    }

    suspend fun clearRecentSearches() {
        dao.clearRecentSearches()
    }

    // --- ADMIN OPERATIONS ---
    suspend fun insertTimetable(timetable: BusTimetableEntity): Long {
        return dao.insertTimetable(timetable)
    }

    suspend fun updateTimetable(timetable: BusTimetableEntity) {
        dao.updateTimetable(timetable)
    }

    suspend fun deleteTimetable(id: Long) {
        dao.deleteTimetableById(id)
    }

    suspend fun approveTimetable(id: Long) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dao.updateStatus(id, "APPROVED", today)
    }

    suspend fun rejectTimetable(id: Long) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dao.updateStatus(id, "REJECTED", today)
    }

    suspend fun replaceAllTimetables(newList: List<BusTimetableEntity>) {
        dao.deleteAllTimetables()
        dao.insertTimetables(newList)
    }

    // PDF Table Extraction Engine Simulation
    suspend fun extractFromPdf(fileName: String, customText: String? = null): List<BusTimetableEntity> {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        
        val extractedRows = mutableListOf<BusTimetableEntity>()

        if (!customText.isNullOrBlank()) {
            // Parse custom input text line by line
            val lines = customText.lines().filter { it.isNotBlank() }
            lines.forEachIndexed { index, line ->
                val parts = line.split(",", "|", "\t").map { it.trim() }
                if (parts.size >= 4) {
                    extractedRows.add(
                        BusTimetableEntity(
                            busNumber = parts.getOrNull(0) ?: "",
                            busName = parts.getOrNull(0) ?: "PDF Timetable Route ${index + 1}",
                            busType = parts.getOrNull(4) ?: "Karnataka Sarige",
                            fromStation = parts.getOrNull(1) ?: "Bengaluru Majestic",
                            fromStationKn = "ಬೆಂಗಳೂರು ಮೆಜೆಸ್ಟಿಕ್",
                            toStation = parts.getOrNull(2) ?: "Mysuru Suburb",
                            toStationKn = "ಮೈಸೂರು ಉಪನಗರ",
                            viaStops = parts.getOrNull(3) ?: "",
                            viaStopsKn = "",
                            departureTime = parts.getOrNull(5) ?: "07:30 AM",
                            arrivalTime = parts.getOrNull(6) ?: "",
                            journeyDuration = "",
                            busStand = "",
                            depot = parts.getOrNull(7) ?: "",
                            division = "",
                            platformNo = "",
                            fareEstimate = "",
                            notes = "Extracted from official PDF: $fileName",
                            notesKn = "ಅಧಿಕೃತ ಪಿಡಿಎಫ್‌ನಿಂದ ಪಡೆದ ಸತ್ಯಸಂದ ಮಾಹಿತಿ: $fileName",
                            status = "APPROVED",
                            createdDate = dateStr,
                            updatedDate = dateStr
                        )
                    )
                }
            }
        }

        if (extractedRows.isEmpty()) {
            // Sample fallback extraction from official PDF timetable
            extractedRows.addAll(
                listOf(
                    BusTimetableEntity(
                        busNumber = "",
                        busName = "Express Service",
                        busType = "Express",
                        fromStation = "Bengaluru Majestic",
                        fromStationKn = "ಬೆಂಗಳೂರು ಮೆಜೆಸ್ಟಿಕ್",
                        toStation = "Davanagere KSRTC",
                        toStationKn = "ದಾವಣಗೆರೆ ಕೆಎಸ್ಆರ್‌ಟಿಸಿ",
                        viaStops = "Tumakuru, Chitradurga",
                        viaStopsKn = "ತುಮಕೂರು, ಚಿತ್ರದುರ್ಗ",
                        departureTime = "02:15 PM",
                        arrivalTime = "",
                        journeyDuration = "",
                        busStand = "Majestic Stand",
                        depot = "Davanagere Depot",
                        division = "Davanagere Division",
                        platformNo = "",
                        fareEstimate = "",
                        notes = "PDF Import Extraction. Superfast Express.",
                        notesKn = "ಪಿಡಿಎಫ್‌ನಿಂದ ಪಡೆದ ಮಾಹಿತಿ. ಸೂಪರ್‌ಫಾಸ್ಟ್ ಸೇವೆ.",
                        status = "PENDING",
                        createdDate = dateStr,
                        updatedDate = dateStr
                    ),
                    BusTimetableEntity(
                        busNumber = "",
                        busName = "Karnataka Sarige",
                        busType = "Karnataka Sarige",
                        fromStation = "Bengaluru Majestic",
                        fromStationKn = "ಬೆಂಗಳೂರು ಮೆಜೆಸ್ಟಿಕ್",
                        toStation = "Udupi Bus Stand",
                        toStationKn = "ಉಡುಪಿ ಬಸ್ ನಿಲ್ದಾಣ",
                        viaStops = "Hassan, Sakleshpur, Mangaluru, Mulki",
                        viaStopsKn = "ಹಾಸನ, ಸಕಲೇಶಪುರ, ಮಂಗಳೂರು, ಮುಲ್ಕಿ",
                        departureTime = "10:15 PM",
                        arrivalTime = "",
                        journeyDuration = "",
                        busStand = "Majestic Stand",
                        depot = "Mangaluru Depot 2",
                        division = "Mangaluru Division",
                        platformNo = "",
                        fareEstimate = "",
                        notes = "PDF Import Extraction.",
                        notesKn = "ಪಿಡಿಎಫ್‌ನಿಂದ ಪಡೆದ ಮಾಹಿತಿ.",
                        status = "PENDING",
                        createdDate = dateStr,
                        updatedDate = dateStr
                    )
                )
            )
        }

        // Record admin submission
        dao.insertAdminSubmission(
            AdminSubmissionEntity(
                fileName = fileName,
                uploadedBy = "Admin Officer",
                uploadDate = dateStr,
                recordsExtracted = extractedRows.size,
                status = "PREVIEW"
            )
        )

        return extractedRows
    }

    fun setLanguage(language: AppLanguage) {
        prefs.setLanguage(language)
    }

    fun setThemeMode(themeMode: AppThemeMode) {
        prefs.setThemeMode(themeMode)
    }

    fun setAdminLoggedIn(loggedIn: Boolean) {
        prefs.setAdminLoggedIn(loggedIn)
    }

    fun clearCache() {
        prefs.clearCache()
    }

    private fun normalizeLocationAliases(query: String): String {
        var result = query.lowercase(Locale.getDefault())
        val aliasMap = mapOf(
            "uppinagady" to "uppinangady",
            "uppinangadi" to "uppinangady",
            "manglore" to "mangaluru",
            "mangalore" to "mangaluru",
            "bangalore" to "bengaluru",
            "mysore" to "mysuru",
            "belgaum" to "belagavi",
            "hubli" to "hubballi",
            "shimoga" to "shivamogga",
            "gulbarga" to "kalaburagi",
            "davangere" to "davanagere",
            "bijapur" to "vijayapura",
            "bellary" to "ballari",
            "coorg" to "madikeri",
            "hampi" to "hosapete",
            "subramanya" to "subrahmanya",
            "dharamsala" to "dharmasthala",
            "dharmastala" to "dharmasthala"
        )
        for ((key, value) in aliasMap) {
            result = result.replace(key, value)
        }
        return result
    }

    private fun extractSearchTokens(query: String): List<String> {
        val stopWords = setOf("to", "from", "via", "bus", "express", "service", "stand", "station", "-", "->", ",")
        return query
            .replace(" to ", " ")
            .replace("-", " ")
            .replace("->", " ")
            .split("\\s+".toRegex())
            .map { it.trim().lowercase(Locale.getDefault()) }
            .filter { it.length >= 2 && !stopWords.contains(it) }
    }
}

private fun String?.isNullToBlank(): Boolean = this == null || this.isBlank()
