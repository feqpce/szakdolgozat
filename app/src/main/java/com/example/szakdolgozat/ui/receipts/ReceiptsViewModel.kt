package com.example.szakdolgozat.ui.receipts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.szakdolgozat.repository.PaymentRepository
import com.example.szakdolgozat.ui.cart.Product
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class ReceiptsViewModel : ViewModel() {
    private val paymentRepo = PaymentRepository()
    private val _receipts = MutableLiveData<List<Receipt>>()
    val receipts: LiveData<List<Receipt>>
        get() = _receipts

    fun getReceipts() {
        paymentRepo.getReceipts()
            .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                val receiptList = mutableListOf<Receipt>()

                querySnapshot?.let { snap ->
                    if (snap.isEmpty) return@addSnapshotListener

                    for (payment in querySnapshot.documents) {
                        val receipt = payment["receipt"] as HashMap<*, *>
                        val items = receipt["items"] as ArrayList<*>

                        receiptList.add(Receipt(receipt["date"] as String, items.map {
                            val item = it as HashMap<*, *>
                            val name =
                                if (item["product_name"] != null) item["product_name"] as String else "Missing name"
                            val price =
                                if (item["price"] != null) (item["price"] as Long).toInt() else -1

                            Product(
                                item["id"] as String,
                                name,
                                price,
                                numberInCart = MutableLiveData((item["amount"] as Long).toInt())
                            )
                        }))

                    }
                }

                _receipts.value = receiptList
            }
    }
}