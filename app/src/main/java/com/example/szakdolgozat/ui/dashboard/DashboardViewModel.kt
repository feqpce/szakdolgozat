package com.example.szakdolgozat.ui.dashboard

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.szakdolgozat.CustomApplication

class DashboardViewModel : ViewModel() {
    private val auth = CustomApplication.getAuthRepository()
    val userName = Transformations.map(auth.user) {
        val toDisplay = it?.displayName ?: "Anon"
        Log.d("DISPLAY", toDisplay)
        return@map toDisplay
    }
}