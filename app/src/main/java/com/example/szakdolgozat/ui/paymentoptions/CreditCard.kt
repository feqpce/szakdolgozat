package com.example.szakdolgozat.ui.paymentoptions

data class CreditCard(
    val cardNumber: String = "",
    val last4: String = "",
    val exp_month: Long = 0,
    val exp_year: Long = 0,
    val cvc: String = ""
)