package com.example.szakdolgozat.ui.cart

import android.util.Log
import androidx.lifecycle.*
import com.example.szakdolgozat.CustomApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartViewModel : ViewModel() {
    private val paymentRepository = CustomApplication.getPaymentRepository()

    val cart = paymentRepository.cart

    val total = Transformations.switchMap(paymentRepository.cart) {

        CombinedLiveData(
            paymentRepository.cart,
            *(it.map { prod -> prod.numberInCart }.toTypedArray())
        ) {

            paymentRepository.cart.value?.let {
                var total = 0

                for (prod in it) {
                    total += prod.price * prod.numberInCart.value!!
                }

                return@CombinedLiveData total
            }
        }
    }

    val checkoutEnabled: LiveData<Boolean>
        get() = Transformations.map(total) {
            it != null && it > 0
        }

    val toastMessage = MutableLiveData<String>()

    fun addProductToCart(productId: String) {
        paymentRepository.addProductToCart(productId)
    }

    fun removeProductFromCart(productId: String) {
        paymentRepository.removeProductFromCart(productId)
    }

    fun checkout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    paymentRepository.checkout(total.value!!).await()
                    toastMessage.postValue("Successful checkout")
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    toastMessage.postValue(e.message)
                }
            }
        }
    }

    companion object {
        const val TAG = "CartViewModel"
    }
}

class CombinedLiveData<R>(
    vararg liveDatas: LiveData<*>,
    private val combine: (datas: List<Any?>) -> R
) : MediatorLiveData<R>() {

    private val datas: MutableList<Any?> = MutableList(liveDatas.size) { null }

    init {
        for (i in liveDatas.indices) {
            super.addSource(liveDatas[i]) {
                datas[i] = it
                value = combine(datas)
            }
        }
    }
}