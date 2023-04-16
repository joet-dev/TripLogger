package com.example.csc202assignment.database

import androidx.room.*
import com.example.csc202assignment.Trip

// The annotation tells Room that this class represents a database in your app.
// The first parameter is a list of entity classes.
// The second parameter is the version of the database.
@Database(entities = [ Trip::class ], version = 1, exportSchema = false)
// Tells the database to use the functions in the class to convert types.
@TypeConverters(TripTypeConverter::class)

/**
 *  TripDatabase class represents the database for this application.
 *
 *  @author Joseph Thurlow
 */
abstract class TripDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
}