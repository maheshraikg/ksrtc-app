package com.example.ksrtc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bus_timetables")
data class BusTimetableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val busNumber: String,
    val busName: String,
    val busType: String, // Karnataka Sarige, Rajahamsa, Airavat, Ambari, Sleeper, Volvo, Express, etc.
    val fromStation: String,
    val fromStationKn: String,
    val toStation: String,
    val toStationKn: String,
    val viaStops: String,
    val viaStopsKn: String,
    val departureTime: String,
    val arrivalTime: String,
    val journeyDuration: String,
    val busStand: String,
    val depot: String,
    val division: String,
    val platformNo: String,
    val fareEstimate: String = "₹ 0",
    val notes: String = "",
    val notesKn: String = "",
    val status: String = "APPROVED", // APPROVED, PENDING, REJECTED
    val createdDate: String = "",
    val updatedDate: String = "",
    val version: Int = 1
)

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nameKn: String,
    val district: String,
    val division: String,
    val isPopular: Boolean = false,
    val pdfUrl: String = "https://ksrtc.karnataka.gov.in/231/Bus%20station%20wise%20services/en",
    val dailyServicesCount: Int = 150
)

@Entity(tableName = "favorite_routes")
data class FavoriteRouteEntity(
    @PrimaryKey
    val timetableId: Long,
    val fromStation: String,
    val toStation: String,
    val busNumber: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromStation: String,
    val toStation: String,
    val busType: String = "All",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_submissions")
data class AdminSubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val uploadedBy: String,
    val uploadDate: String,
    val recordsExtracted: Int,
    val status: String // PREVIEW, APPROVED, REJECTED
)

enum class AppLanguage {
    ENGLISH, KANNADA
}

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

data class BusFilterState(
    val selectedBusType: String = "All",
    val sortBy: String = "Departure Time", // Departure Time, Bus Type, Duration
    val searchQuery: String = "",
    val fromStation: String = "",
    val toStation: String = ""
)
