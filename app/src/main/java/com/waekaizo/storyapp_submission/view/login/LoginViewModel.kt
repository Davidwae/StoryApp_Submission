package com.waekaizo.storyapp_submission.view.login

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waekaizo.storyapp_submission.data.LoginRepository
import com.waekaizo.storyapp_submission.data.local.UserModel
import kotlinx.coroutines.launch

class LoginViewModel (private val loginRepository: LoginRepository): ViewModel(){
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            loginRepository.saveSession(user)
        }
    }

    fun login(email: String, password: String) = loginRepository.login(email, password)
}