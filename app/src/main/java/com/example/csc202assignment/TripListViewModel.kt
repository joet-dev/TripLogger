package com.example.csc202assignment

import androidx.lifecycle.*

/**
 * TripListViewModel is used to store a list of Trip objects and
 * manage database queries (through tripRepository) for the TripListFragment.
 *
 * @author Joseph Thurlow
 */
class TripListViewModel : ViewModel() {

    private val tripRepository = TripRepository.get()
    val tripListLiveData = tripRepository.getTrips()

    fun addTrip(trip: Trip) {
        tripRepository.addTrip(trip)
    }

    fun deleteAllTrips() {
        tripRepository.deleteAllTrips()
    }
}