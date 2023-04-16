package com.example.csc202assignment

import androidx.lifecycle.*
import java.io.File
import java.util.*

/**
 * TripDetailViewModel is used to manage database queries (through tripRepository) without being
 * affected by fragment and activity updates.
 *
 * @author Joseph Thurlow
 */
class TripDetailViewModel: ViewModel() {

    // Stores a handle to the tripRepository.
    private val tripRepository = TripRepository.get()
    private val tripIdLiveData = MutableLiveData<UUID>()

    var tripLiveData: LiveData<Trip?> =
        Transformations.switchMap(tripIdLiveData) { tripId ->
            tripRepository.getTrip(tripId)
        }

    fun loadTrip(tripId: UUID) {
        tripIdLiveData.value = tripId
    }

    fun saveTrip(trip: Trip) {
        tripRepository.updateTrip(trip)
    }

    fun getPhotoFile(trip: Trip): File {
        return tripRepository.getPhotoFile(trip)
    }

    fun deleteTrip(trip: Trip) {
        tripRepository.deleteTrip(trip.id)
    }
}