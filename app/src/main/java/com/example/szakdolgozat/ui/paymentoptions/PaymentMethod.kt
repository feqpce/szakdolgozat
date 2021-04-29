package com.example.szakdolgozat.ui.paymentoptions

data class PaymentMethod(
    val id: String,
    val card: CreditCard? = null
)