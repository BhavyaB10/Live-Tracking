package com.example.livetracking.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocationDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "location.db"

        private const val CURRENT_SESSION_TABLE = "current_session"
        private const val PREVIOUS_SESSION_TABLE = "previous_session"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_DISPLACEMENT = "displacement"
        private const val COLUMN_SPEED = "speed"
        private const val COLUMN_TIMESTAMP = "timestamp"

        private const val COLUMN_START_TIME = "start_time"
        private const val COLUMN_START_LAT = "start_lat"
        private const val COLUMN_START_LONG = "start_long"
        private const val COLUMN_END_LAT = "end_lat"
        private const val COLUMN_END_LONG = "end_long"
        private const val COLUMN_END_TIME = "end_time"
        private const val COLUMN_TOTAL_DISTANCE = "total_distance"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCurrentSessionTable = ("CREATE TABLE $CURRENT_SESSION_TABLE ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_LATITUDE REAL, "
                + "$COLUMN_LONGITUDE REAL, "
                + "$COLUMN_DISPLACEMENT REAL, "
                + "$COLUMN_SPEED REAL, "
                + "$COLUMN_TIMESTAMP INTEGER)")

        val createPreviousSessionTable = ("CREATE TABLE $PREVIOUS_SESSION_TABLE ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_START_TIME REAL, "
                + "$COLUMN_START_LAT REAL, "
                + "$COLUMN_START_LONG REAL, "
                + "$COLUMN_END_LAT REAL, "
                + "$COLUMN_END_LONG REAL, "
                + "$COLUMN_END_TIME REAL, "
                + "$COLUMN_TOTAL_DISTANCE REAL)")

        db.execSQL(createCurrentSessionTable)
        db.execSQL(createPreviousSessionTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $CURRENT_SESSION_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $PREVIOUS_SESSION_TABLE")
        onCreate(db)
    }

    fun addLocationToCurrentSession(latitude: Double, longitude: Double, displacement: Float, speed: Float, timestamp: Long) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
            put(COLUMN_DISPLACEMENT, displacement)
            put(COLUMN_SPEED, speed)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        db.insert(CURRENT_SESSION_TABLE, null, values)
        db.close()
    }

    fun getAllCurrentSessionLocations(): Cursor {
        val db = this.readableDatabase
        return db.query(CURRENT_SESSION_TABLE, null, null, null, null, null, null)
    }

    fun getAllPreviousSessionLocations(): Cursor {
        val db = this.readableDatabase
        return db.query(PREVIOUS_SESSION_TABLE, null, null, null, null, null, null)
    }


    fun getLastLocationFromCurrentSession(): LocationEvent? {
        val query = "SELECT * FROM $CURRENT_SESSION_TABLE ORDER BY $COLUMN_ID DESC LIMIT 1"
        val cursor: Cursor = readableDatabase.rawQuery(query, null)

        return cursor.use {
            val idIndex = it.getColumnIndex(COLUMN_ID)
            val latitudeIndex = it.getColumnIndex(COLUMN_LATITUDE)
            val longitudeIndex = it.getColumnIndex(COLUMN_LONGITUDE)
            val displacementIndex = it.getColumnIndex(COLUMN_DISPLACEMENT)
            val speedIndex = it.getColumnIndex(COLUMN_SPEED)
            val timestampIndex = it.getColumnIndex(COLUMN_TIMESTAMP)

            if (idIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1 && displacementIndex != -1 &&
                speedIndex != -1 && timestampIndex != -1 && it.moveToFirst()
            ) {
                val id = it.getInt(idIndex)
                val latitude = it.getDouble(latitudeIndex)
                val longitude = it.getDouble(longitudeIndex)
                val displacement = it.getFloat(displacementIndex)
                val speed = it.getFloat(speedIndex)
                val timestamp = it.getLong(timestampIndex)
                LocationEvent(id, latitude, longitude, displacement, speed, timestamp)
            } else {
                null
            }
        }
    }

    fun moveCurrentSessionToPrevious() {
        val db = writableDatabase
        db.execSQL("""
    INSERT INTO $PREVIOUS_SESSION_TABLE (
        $COLUMN_START_TIME,
        $COLUMN_START_LAT,
        $COLUMN_START_LONG,
        $COLUMN_END_LAT,
        $COLUMN_END_LONG,
        $COLUMN_END_TIME,
        $COLUMN_TOTAL_DISTANCE
    )
    SELECT 
        MIN($COLUMN_TIMESTAMP) AS $COLUMN_START_TIME,
        (SELECT $COLUMN_LATITUDE FROM $CURRENT_SESSION_TABLE ORDER BY $COLUMN_TIMESTAMP ASC LIMIT 1) AS $COLUMN_START_LAT,
        (SELECT $COLUMN_LONGITUDE FROM $CURRENT_SESSION_TABLE ORDER BY $COLUMN_TIMESTAMP ASC LIMIT 1) AS $COLUMN_START_LONG,
        (SELECT $COLUMN_LATITUDE FROM $CURRENT_SESSION_TABLE ORDER BY $COLUMN_TIMESTAMP DESC LIMIT 1) AS $COLUMN_END_LAT,
        (SELECT $COLUMN_LONGITUDE FROM $CURRENT_SESSION_TABLE ORDER BY $COLUMN_TIMESTAMP DESC LIMIT 1) AS $COLUMN_END_LONG,
        MAX($COLUMN_TIMESTAMP) AS $COLUMN_END_TIME,
        SUM($COLUMN_DISPLACEMENT) AS $COLUMN_TOTAL_DISTANCE
       FROM $CURRENT_SESSION_TABLE
       WHERE $COLUMN_LATITUDE != 0.0  AND $COLUMN_LONGITUDE != 0.0 ;
""")

        //clearCurrentSession()

    }

    fun clearCurrentSession() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $CURRENT_SESSION_TABLE")
    }

//    fun clearPreviousSession() {
//        val db = writableDatabase
//        db.execSQL("DELETE FROM $PREVIOUS_SESSION_TABLE")
//    }
}

