package com.example.snaptask.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Robust Native SQLite OpenHelper.
 * Provides 100% full relational SQL storage for local screenshots without requiring annotation processors.
 */
class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context.applicationContext, "snaptask_local.db", null, 1) {

    private val _dataVersion = MutableStateFlow(0L)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS local_screenshots (
                id INTEGER PRIMARY KEY,
                contentUri TEXT NOT NULL,
                displayName TEXT NOT NULL,
                capturedAt INTEGER NOT NULL,
                size INTEGER NOT NULL,
                ocrStatus TEXT NOT NULL,
                ocrText TEXT,
                category TEXT NOT NULL DEFAULT 'Other',
                eventDate TEXT,
                eventTime TEXT,
                confidence REAL NOT NULL DEFAULT 0.0,
                backendId INTEGER,
                status TEXT NOT NULL DEFAULT 'active'
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS local_screenshots")
        onCreate(db)
    }

    private fun notifyDataChanged() {
        _dataVersion.value = System.currentTimeMillis()
    }

    suspend fun insertOrUpdate(screenshot: LocalScreenshotEntity) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put("id", screenshot.id)
            put("contentUri", screenshot.contentUri)
            put("displayName", screenshot.displayName)
            put("capturedAt", screenshot.capturedAt)
            put("size", screenshot.size)
            put("ocrStatus", screenshot.ocrStatus)
            put("ocrText", screenshot.ocrText)
            put("category", screenshot.category)
            put("eventDate", screenshot.eventDate)
            put("eventTime", screenshot.eventTime)
            put("confidence", screenshot.confidence)
            put("backendId", screenshot.backendId)
            put("status", screenshot.status)
        }
        writableDatabase.insertWithOnConflict("local_screenshots", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        notifyDataChanged()
    }

    suspend fun insertAll(screenshots: List<LocalScreenshotEntity>) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (s in screenshots) {
                val values = ContentValues().apply {
                    put("id", s.id)
                    put("contentUri", s.contentUri)
                    put("displayName", s.displayName)
                    put("capturedAt", s.capturedAt)
                    put("size", s.size)
                    put("ocrStatus", s.ocrStatus)
                    put("ocrText", s.ocrText)
                    put("category", s.category)
                    put("eventDate", s.eventDate)
                    put("eventTime", s.eventTime)
                    put("confidence", s.confidence)
                    put("backendId", s.backendId)
                    put("status", s.status)
                }
                db.insertWithOnConflict("local_screenshots", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        notifyDataChanged()
    }

    suspend fun getAllActive(): List<LocalScreenshotEntity> = withContext(Dispatchers.IO) {
        val list = mutableListOf<LocalScreenshotEntity>()
        readableDatabase.rawQuery("SELECT * FROM local_screenshots WHERE status != 'trashed' ORDER BY capturedAt DESC", null)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow("id")
            val uriCol = cursor.getColumnIndexOrThrow("contentUri")
            val nameCol = cursor.getColumnIndexOrThrow("displayName")
            val capCol = cursor.getColumnIndexOrThrow("capturedAt")
            val sizeCol = cursor.getColumnIndexOrThrow("size")
            val ocrStatCol = cursor.getColumnIndexOrThrow("ocrStatus")
            val ocrTextCol = cursor.getColumnIndexOrThrow("ocrText")
            val catCol = cursor.getColumnIndexOrThrow("category")
            val dateCol = cursor.getColumnIndexOrThrow("eventDate")
            val timeCol = cursor.getColumnIndexOrThrow("eventTime")
            val confCol = cursor.getColumnIndexOrThrow("confidence")
            val backCol = cursor.getColumnIndexOrThrow("backendId")
            val statCol = cursor.getColumnIndexOrThrow("status")

            while (cursor.moveToNext()) {
                list.add(LocalScreenshotEntity(
                    id = cursor.getLong(idCol),
                    contentUri = cursor.getString(uriCol),
                    displayName = cursor.getString(nameCol),
                    capturedAt = cursor.getLong(capCol),
                    size = cursor.getLong(sizeCol),
                    ocrStatus = cursor.getString(ocrStatCol),
                    ocrText = cursor.getString(ocrTextCol),
                    category = cursor.getString(catCol),
                    eventDate = cursor.getString(dateCol),
                    eventTime = cursor.getString(timeCol),
                    confidence = cursor.getFloat(confCol),
                    backendId = if (cursor.isNull(backCol)) null else cursor.getLong(backCol),
                    status = cursor.getString(statCol),
                ))
            }
        }
        list
    }

    suspend fun getAllIds(): List<Long> = withContext(Dispatchers.IO) {
        val ids = mutableListOf<Long>()
        readableDatabase.rawQuery("SELECT id FROM local_screenshots", null)?.use { cursor ->
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(0))
            }
        }
        ids
    }

    suspend fun pruneMissingActive(validIds: Set<Long>) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        if (validIds.isEmpty()) {
            db.execSQL("DELETE FROM local_screenshots WHERE status = 'active'")
        } else {
            val placeholders = validIds.joinToString(",") { "?" }
            val args = validIds.map { it.toString() }.toTypedArray()
            db.execSQL("DELETE FROM local_screenshots WHERE id NOT IN ($placeholders) AND status = 'active'", args)
        }
        notifyDataChanged()
    }

    suspend fun markAsTrashed(ids: List<Long>) = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext
        val placeholders = ids.joinToString(",") { "?" }
        val args = ids.map { it.toString() }.toTypedArray()
        writableDatabase.execSQL("UPDATE local_screenshots SET status = 'trashed' WHERE id IN ($placeholders)", args)
        notifyDataChanged()
    }

    suspend fun restoreFromTrash(ids: List<Long>) = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext
        val placeholders = ids.joinToString(",") { "?" }
        val args = ids.map { it.toString() }.toTypedArray()
        writableDatabase.execSQL("UPDATE local_screenshots SET status = 'active' WHERE id IN ($placeholders)", args)
        notifyDataChanged()
    }

    suspend fun deletePermanently(ids: List<Long>) = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext
        val placeholders = ids.joinToString(",") { "?" }
        val args = ids.map { it.toString() }.toTypedArray()
        writableDatabase.execSQL("DELETE FROM local_screenshots WHERE id IN ($placeholders)", args)
        notifyDataChanged()
    }

    suspend fun clearAllTables() = withContext(Dispatchers.IO) {
        writableDatabase.execSQL("DELETE FROM local_screenshots")
        notifyDataChanged()
    }

    val liveDataVersion: Flow<Long> = _dataVersion.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context)
                INSTANCE = instance
                instance
            }
        }
    }
}