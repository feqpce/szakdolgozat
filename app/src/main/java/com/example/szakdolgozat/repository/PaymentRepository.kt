package com.example.szakdolgozat.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.szakdolgozat.CustomApplication
import com.example.szakdolgozat.ui.cart.Product
import com.example.szakdolgozat.ui.paymentoptions.CreditCard
import com.example.szakdolgozat.ui.paymentoptions.PaymentMethod
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult

class PaymentRepository {
    private val scannedProductIDs = mutableListOf<String>()
    private val auth = CustomApplication.getAuthRepository()
    private val fireStore = FirebaseFirestore.getInstance()
    private val functions = FirebaseFunctions.getInstance()

    private val firebaseUserObserver: (t: FirebaseUser?) -> Unit = {
        if (it != null) {
            initPaymentMethodList(it.uid)
            initDefaultPaymentMethodListener(it.uid)
        } else {
            paymentMethodsListener?.remove()
            defaultPaymentMethodListener?.remove()
        }
    }
    private var paymentMethodsListener: ListenerRegistration? = null
    private var defaultPaymentMethodListener: ListenerRegistration? = null

    private val productsInCart = mutableListOf<Product>()

    private val _cart = MutableLiveData(productsInCart)
    val cart: LiveData<MutableList<Product>>
        get() = _cart

    private val _paymentMethods: MutableLiveData<List<PaymentMethod>> = MutableLiveData()
    val paymentMethods: LiveData<List<PaymentMethod>>
        get() = _paymentMethods

    private val _defaultPaymentMethod = MutableLiveData<String>()
    val defaultPaymentMethod: LiveData<String>
        get() = _defaultPaymentMethod

    init {
        auth.user.observeForever(firebaseUserObserver)
    }

    fun initPaymentMethodList(currUser: String) {
        paymentMethodsListener = fireStore.collection("/stripe_customers/$currUser/payment_methods")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val paymentOptions = mutableListOf<PaymentMethod>()

                querySnapshot?.documents?.map { documentSnapshot ->

                    documentSnapshot.data?.let { paymentMethodFields ->

                        Log.d(TAG, paymentMethodFields.toString())
                        if (paymentMethodFields["card"] == null)
                            return@map
                        val card = paymentMethodFields["card"] as HashMap<*, *>
                        val payment = PaymentMethod(
                            paymentMethodFields["id"] as String,
                            CreditCard(
                                last4 = card["last4"] as String,
                                exp_month = card["exp_month"] as Long,
                                exp_year = card["exp_year"] as Long
                            )
                        )
                        paymentOptions.add(payment)


                    }
                }
                _paymentMethods.value = paymentOptions
            }
    }

    fun initDefaultPaymentMethodListener(currUser: String) {
        defaultPaymentMethodListener = fireStore.document("/stripe_customers/$currUser")
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

                _defaultPaymentMethod.value = documentSnapshot?.getString("default_payment_method")
            }
    }

    fun createPaymentMethod(creditCard: HashMap<String, Any>) {
        functions.getHttpsCallable("createPaymentMethod").call(creditCard).addOnCompleteListener {
            it.result?.let {
                Log.d("REPO", it.data.toString())
            }
        }
    }

    fun addProductToCart(productId: String) {
        if (scannedProductIDs.contains(productId))
            return
        scannedProductIDs.add(productId)

        functions.getHttpsCallable("addProductToCart").call(productId).addOnSuccessListener {
            val fields = it.data as HashMap<*, *>

            productsInCart.add(
                Product(
                    productId,
                    fields["product_name"] as String,
                    fields["price"] as Int,
                    MutableLiveData(1)
                )
            )

            _cart.value = productsInCart
        }
    }

    fun removeProductFromCart(productId: String) {
        productsInCart.remove(productsInCart.find { prod -> prod.id == productId })
        scannedProductIDs.remove(productId)
        _cart.value = mutableListOf(*(productsInCart.toTypedArray()))
    }

    fun checkout(total: Int): Task<HttpsCallableResult> {
        val productHashes = mutableListOf<HashMap<String, Any>>()
        for (product in productsInCart) {
            productHashes.add(
                hashMapOf(
                    "id" to product.id,
                    "amount" to product.numberInCart.value!!
                )
            )
        }
        return functions.getHttpsCallable("checkout").call(
            hashMapOf(
                "items" to productHashes,
                "total" to total
            )
        )
    }

    fun setDefaultPaymentMethod(method: PaymentMethod): Task<Void> {
        return fireStore.document("stripe_customers/${auth.user.value!!.uid}")
            .update("default_payment_method", method.id)
    }

    companion object {
        const val TAG = "PaymentRepository"
    }
}