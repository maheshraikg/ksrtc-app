package com.example.ksrtc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ksrtc.data.model.AdminSubmissionEntity
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.model.FavoriteRouteEntity
import com.example.ksrtc.data.model.RecentSearchEntity
import com.example.ksrtc.data.model.StationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BusTimetableEntity::class,
        StationEntity::class,
        FavoriteRouteEntity::class,
        RecentSearchEntity::class,
        AdminSubmissionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ksrtcDao(): KsrtcDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ksrtc_timings_db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val dao = database.ksrtcDao()
                        dao.insertStations(DefaultKsrtcData.initialStations)
                        dao.insertTimetables(DefaultKsrtcData.initialTimetables)
                    }
                }
            }
        }
    }
}
