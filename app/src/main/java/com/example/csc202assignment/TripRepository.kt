package com.example.csc202assignment

import android.content.*
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.csc202assignment.database.TripDatabase
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "trip-database"

/**
 * The TripRepository handles storage and retrieval of data from the TripDatabase through tripDao.
 *
 * @author Joseph Thurlow
 */
class TripRepository private constructor(context: Context) {

    // Stores references to your database and DAO objects.
    // DatabaseBuilder creates a concrete implementation of your abstract TripDatabase.
    private val database : TripDatabase = Room.databaseBuilder(
        context.applicationContext,
        TripDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val tripDao = database.tripDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    fun getTrips(): LiveData<List<Trip>> = tripDao.getTrips()

    fun getTrip(id: UUID): LiveData<Trip?> = tripDao.getTrip(id)

    fun updateTrip(trip: Trip) {
        executor.execute {
            tripDao.updateTrip(trip)
        }
    }
    fun addTrip(trip: Trip) {
        executor.execute {
            tripDao.addTrip(trip)
        }
    }

    fun deleteTrip(id: UUID) {
        executor.execute {
            tripDao.deleteTrip(id)
        }
    }

    fun deleteAllTrips() {
        executor.execute {
            tripDao.deleteTable()
        }
    }

    fun getPhotoFile(trip: Trip): File = File(filesDir, trip.imageFileName)

    //Singleton
    companion object {
        private var INSTANCE: TripRepository? = null

        fun initialize(context: Context) {
            // Only creates an instance if there is no instance already created.
            if (INSTANCE == null) {
                INSTANCE = TripRepository(context)
            }
        }

        fun get(): TripRepository {
            // Throws exception when initialize hasn't been called beforehand.
            return INSTANCE ?: throw IllegalStateException("TripRepository must be initialized")
        }
    }
}