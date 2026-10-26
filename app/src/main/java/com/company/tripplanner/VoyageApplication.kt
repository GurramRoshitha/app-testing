package com.company.tripplanner

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class VoyageApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("VoyageApplication", "FirebaseApp successfully initialized on startup")
        } catch (e: Exception) {
            Log.e("VoyageApplication", "Error during startup FirebaseApp initialization", e)
        }
    }
}