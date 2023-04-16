package com.example.csc202assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

/**
 * The main activity hosts trip fragments.
 *
 * @author Joseph Thurlow
 */
class MainActivity : AppCompatActivity(), TripListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adds a fragment to the fragment manager.
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = TripListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    // Replaces the fragment hosted in the activity with the new fragment provided.
    override fun onTripSelected(tripId: UUID, newTrip: Boolean) {
        val fragment = TripFragment.newInstance(tripId, newTrip)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}