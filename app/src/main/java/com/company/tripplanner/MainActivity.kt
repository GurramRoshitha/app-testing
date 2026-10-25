package com.company.tripplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.company.tripplanner.navigation.AppNavigator
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.utils.AppTheme
import com.company.tripplanner.utils.ThemeManager
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("VOYAGE_CRASH", "Firebase Initialization Failed: ${e.message}", e)
        }

        themeManager = ThemeManager(this)
        enableEdgeToEdge()

        setContent {
            val themeState by themeManager.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
            
            val useDarkTheme = when (themeState) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                AppNavigator(themeManager)
            }
        }
    }
}
