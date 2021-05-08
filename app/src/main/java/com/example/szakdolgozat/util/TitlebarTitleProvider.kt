package com.example.szakdolgozat.util

import com.example.szakdolgozat.R

object TitlebarTitleProvider {

    fun getTitle(destinationId: Int): String {
        return when (destinationId) {
            R.id.bottom_nav_dashboard -> "Dashboard"
            R.id.bottom_nav_cart -> "Shopping cart"
            R.id.bottom_nav_receipts -> "Receipts"
            R.id.bottom_nav_payment_options -> "Payment methods"
            R.id.bottom_nav_settings -> "Settings"
            R.id.loginFragment -> "Login"
            else -> "Unknown destination"
        }
    }
}