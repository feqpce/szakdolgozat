package com.example.szakdolgozat.ui.paymentoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.szakdolgozat.CustomApplication
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentOptionsViewModel : ViewModel() {
    private val repository = CustomApplication.getPaymentRepository()

    val paymentMethods = repository.paymentMethods

    val defaultPaymentMethod = repository.defaultPaymentMethod

    fun setDefaultPaymentMethod(method: PaymentMethod) {
        viewModelScope.launch {
            repository.setDefaultPaymentMethod(method).await()
        }
    }
}