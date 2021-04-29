package com.example.szakdolgozat.ui.login

import androidx.lifecycle.*
import com.example.szakdolgozat.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val _progressBarVisible = MutableLiveData(false)
    val progressBarVisible
        get() = _progressBarVisible

    private val _result = MutableLiveData("")
    val result
        get() = _result

    fun register(email: String, password: String) {
        var result: String

        viewModelScope.launch {
            result = withContext(Dispatchers.IO) { authRepository.register(email, password) }
            _result.value = result

        }
    }

    fun login(email: String, password: String) {
        var result: String?

        _progressBarVisible.value = true
        viewModelScope.launch {
            result = withContext(Dispatchers.IO) { authRepository.login(email, password) }
            _progressBarVisible.value = false
            result?.let {
                _result.value = it
            }
        }
    }

    fun loginWithGoogle(credential: AuthCredential) {
        var result: String?

        _progressBarVisible.value = true
        viewModelScope.launch {
            result = withContext(Dispatchers.IO) { authRepository.signInWithGoogle(credential) }
            _progressBarVisible.value = false
            result?.let {
                _result.value = it
            }
        }
    }

    fun resetPassword(email: String) {
        authRepository.resetPassword(email).addOnCompleteListener {
            with(it.exception) {
                if (this != null) {
                    _result.value = this.message
                    return@addOnCompleteListener
                }
            }
            _result.value = "Password reset email sent."
        }
    }
}