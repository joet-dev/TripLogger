package com.example.csc202assignment

import android.app.Application

/**
 * The TripLoggerApplication is created on application launch and destroyed when the application
 * process is destroyed.
 *
 * @author Joseph Thurlow
 */
class TripLoggerApplication : Application() {

    // This is called by the system when your application is first loaded into memory.
    override fun onCreate() {
        super.onCreate()
        TripRepository.initialize(this)
    }
}