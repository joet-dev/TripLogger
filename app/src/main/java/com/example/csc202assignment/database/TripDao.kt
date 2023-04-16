package com.example.csc202assignment.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.csc202assignment.Trip
import java.util.*

/**
 *  TripDao is an interface that defines database functions that implement SQL commands.
 *
 *  @author Joseph Thurlow
 */
// The annotation lets Room know that this is one of your data access objects.
@Dao
interface TripDao {

    // This annotation indicates what a function is doing to the database.
    // The parameter is an SQL command.
    // The LiveData class executes the query on a background thread.
    @Query("SELECT * FROM trip")
    fun getTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trip WHERE id=(:id)")
    fun getTrip(id: UUID): LiveData<Trip?>

    // Takes in a trip object, uses the ID stored in that trip to find the associated row, then
    // updates the data in that row based on the new data in the trip object.
    @Update
    fun updateTrip(trip: Trip)

    @Insert
    fun addTrip(trip: Trip)

    @Query("DELETE FROM trip WHERE id=(:id)")
    fun deleteTrip(id: UUID)

    @Query("DELETE FROM trip")
    fun deleteTable()
}