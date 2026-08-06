package com.example.ksrtc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ksrtc.data.model.AdminSubmissionEntity
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.model.FavoriteRouteEntity
import com.example.ksrtc.data.model.RecentSearchEntity
import com.example.ksrtc.data.model.StationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KsrtcDao {

    // --- TIMETABLES ---
    @Query("SELECT * FROM bus_timetables WHERE status = 'APPROVED' ORDER BY id ASC")
    fun getAllApprovedTimetables(): Flow<List<BusTimetableEntity>>

    @Query("SELECT * FROM bus_timetables ORDER BY id DESC")
    fun getAllTimetablesAdmin(): Flow<List<BusTimetableEntity>>

    @Query("SELECT * FROM bus_timetables WHERE id = :id")
    suspend fun getTimetableById(id: Long): BusTimetableEntity?

    @Query("""
        SELECT * FROM bus_timetables 
        WHERE status = 'APPROVED'
        AND (
            LOWER(fromStation) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(fromStationKn) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(toStation) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(toStationKn) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(busNumber) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(busName) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(viaStops) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(viaStopsKn) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(busStand) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(busType) LIKE '%' || LOWER(:query) || '%'
        )
        ORDER BY departureTime ASC
    """)
    fun searchTimetables(query: String): Flow<List<BusTimetableEntity>>

    @Query("""
        SELECT * FROM bus_timetables 
        WHERE status = 'APPROVED'
        AND (LOWER(fromStation) LIKE '%' || LOWER(:from) || '%' OR LOWER(fromStationKn) LIKE '%' || LOWER(:from) || '%')
        AND (LOWER(toStation) LIKE '%' || LOWER(:to) || '%' OR LOWER(toStationKn) LIKE '%' || LOWER(:to) || '%')
        ORDER BY departureTime ASC
    """)
    fun getTimetablesBetween(from: String, to: String): Flow<List<BusTimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: BusTimetableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetables(timetables: List<BusTimetableEntity>)

    @Update
    suspend fun updateTimetable(timetable: BusTimetableEntity)

    @Query("DELETE FROM bus_timetables WHERE id = :id")
    suspend fun deleteTimetableById(id: Long)

    @Query("DELETE FROM bus_timetables")
    suspend fun deleteAllTimetables()

    @Query("UPDATE bus_timetables SET status = :status, updatedDate = :updatedDate WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedDate: String)

    @Query("SELECT COUNT(*) FROM bus_timetables WHERE status = 'APPROVED'")
    fun getActiveTimetablesCount(): Flow<Int>

    // --- STATIONS ---
    @Query("SELECT * FROM stations ORDER BY isPopular DESC, name ASC")
    fun getAllStations(): Flow<List<StationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    // --- FAVORITES ---
    @Query("SELECT * FROM favorite_routes ORDER BY addedTimestamp DESC")
    fun getFavoriteRoutes(): Flow<List<FavoriteRouteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_routes WHERE timetableId = :timetableId)")
    fun isFavorite(timetableId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteRouteEntity)

    @Query("DELETE FROM favorite_routes WHERE timetableId = :timetableId")
    suspend fun removeFavorite(timetableId: Long)

    // --- RECENT SEARCHES ---
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(recentSearch: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()

    // --- ADMIN SUBMISSIONS ---
    @Query("SELECT * FROM admin_submissions ORDER BY id DESC")
    fun getAdminSubmissions(): Flow<List<AdminSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminSubmission(submission: AdminSubmissionEntity): Long

    @Query("UPDATE admin_submissions SET status = :status WHERE id = :id")
    suspend fun updateSubmissionStatus(id: Long, status: String)
}
