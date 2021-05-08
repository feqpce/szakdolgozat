package com.example.szakdolgozat.ui.receipts

import com.example.szakdolgozat.ui.cart.Product

data class Receipt(
    val date: String,
    val items: List<Product>
)
