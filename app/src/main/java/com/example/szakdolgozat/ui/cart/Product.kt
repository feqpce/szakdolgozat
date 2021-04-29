package com.example.szakdolgozat.ui.cart

import androidx.lifecycle.MutableLiveData

data class Product(
    val id: String,
    val productName: String,
    val price: Int,
    val numberInCart: MutableLiveData<Int> = MutableLiveData(0)
)