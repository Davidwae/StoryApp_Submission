package com.waekaizo.storyapp_submission.view.register

import androidx.lifecycle.ViewModel
import com.waekaizo.storyapp_submission.data.LoginRepository

class RegisterViewModel(private val repository: LoginRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}