package com.nestorgarcia.nodocivico.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nestorgarcia.nodocivico.data.local.dao.*
import com.nestorgarcia.nodocivico.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Category::class,
        Report::class,
        ReportStatusHistory::class,
        Reminder::class,
        SyncEvent::class,
        FollowUp::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reportDao(): ReportDao
    abstract fun reportStatusHistoryDao(): ReportStatusHistoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun syncEventDao(): SyncEventDao
    abstract fun followUpDao(): FollowUpDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nodo_civico_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Poblar categorías iniciales al crear la BD
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedCategories(database.categoryDao())
                }
            }
        }

        suspend fun seedCategories(categoryDao: CategoryDao) {
            if (categoryDao.count() > 0) return
            val categories = listOf(
                Category(name = "Alumbrado",   iconCode = "💡", colorHex = "#F59E0B"),
                Category(name = "Aseo",        iconCode = "🧹", colorHex = "#10B981"),
                Category(name = "Seguridad",   iconCode = "🔒", colorHex = "#DC2626"),
                Category(name = "Servicios",   iconCode = "🔧", colorHex = "#2563EB"),
                Category(name = "Vías",        iconCode = "🛣️", colorHex = "#7C3AED"),
                Category(name = "Otro",        iconCode = "📌", colorHex = "#64748B")
            )
            categoryDao.insertAll(categories)
        }
    }
}