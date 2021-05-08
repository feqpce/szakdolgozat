package com.example.szakdolgozat.ui.cart

import androidx.lifecycle.MutableLiveData

data class Product(
    val id: String,
    val productName: String? = "Missing name",
    val price: Int = 0,
    val numberInCart: MutableLiveData<Int> = MutableLiveData(0)
) {
    override fun toString(): String {
        return "$productName ($price Ft) - ${numberInCart.value} db"
    }
}