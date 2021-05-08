package com.example.szakdolgozat.ui.paymentoptions

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.szakdolgozat.R
import com.example.szakdolgozat.repository.PaymentRepository
import java.lang.Long.parseLong

class CreditCardDetailsActivity : AppCompatActivity() {

    private val repository = PaymentRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card_details)
        supportActionBar?.title = "Add credit card"

        val saveButton = findViewById<AppCompatButton>(R.id.button_save_card)

        saveButton.setOnClickListener {
            saveCreditCard()
        }
    }

    private fun saveCreditCard() {
        val cardNumber = findViewById<EditText>(R.id.card_number_text_input).text.toString()
        val expMonth = parseLong(findViewById<EditText>(R.id.exp_month_text_input).text.toString())
        val expYear = parseLong(findViewById<EditText>(R.id.exp_year_text_input).text.toString())
        val cvc = findViewById<EditText>(R.id.cvc_text_input).text.toString()

        val creditCard = hashMapOf<String, Any>(
            "card_number" to cardNumber,
            "exp_month" to expMonth,
            "exp_year" to expYear,
            "cvc" to cvc
        )
        repository.createPaymentMethod(creditCard)

        finish()
    }
}