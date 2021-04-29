package com.example.szakdolgozat

import android.app.Application
import com.example.szakdolgozat.repository.AuthRepository
import com.example.szakdolgozat.repository.PaymentRepository

class CustomApplication : Application() {
    companion object {
        private var authRepository: AuthRepository? = null
        private var paymentRepository: PaymentRepository? = null

        fun getAuthRepository(): AuthRepository {
            var repo = authRepository
            if (repo == null) {
                repo = AuthRepository()
                authRepository = repo
            }
            return repo
        }

        fun getPaymentRepository(): PaymentRepository {
            var repo = paymentRepository
            if (repo == null) {
                repo = PaymentRepository()
                paymentRepository = repo
            }
            return repo
        }
    }
}