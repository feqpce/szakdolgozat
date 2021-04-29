package com.example.szakdolgozat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val TAG = "AUTH_REPOSITORY"
    private val auth = FirebaseAuth.getInstance()

    private val _user: MutableLiveData<FirebaseUser?> = MutableLiveData(auth.currentUser)
    val user: LiveData<FirebaseUser?>
        get() = _user

    init {
        auth.addAuthStateListener { state ->
            _user.value = state.currentUser
        }
    }

    suspend fun register(email: String, password: String): String {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            "Successful registration"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun login(email: String, password: String): String? {
        var error: String? = null
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            error = e.message
        } finally {
            return error
        }
    }

    suspend fun signInWithGoogle(credential: AuthCredential): String? {
        var error: String? = null
        try {
            auth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            error = e.message
        } finally {
            return error
        }
    }

    fun resetPassword(email: String): Task<Void> {
        auth.useAppLanguage()
        return auth.sendPasswordResetEmail(email)
    }

    fun logout() {
        auth.signOut()
    }
}