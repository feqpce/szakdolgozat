package com.example.szakdolgozat.ui.dashboard

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
}