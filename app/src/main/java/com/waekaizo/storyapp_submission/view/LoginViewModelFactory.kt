package com.waekaizo.storyapp_submission.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waekaizo.storyapp_submission.data.LoginRepository
import com.waekaizo.storyapp_submission.data.di.Injection
import com.waekaizo.storyapp_submission.view.addstory.AddStoryViewModel
import com.waekaizo.storyapp_submission.view.login.LoginViewModel
import com.waekaizo.storyapp_submission.view.main.MainViewModel
import com.waekaizo.storyapp_submission.view.maps.MapsViewModel
import com.waekaizo.storyapp_submission.view.register.RegisterViewModel

class LoginViewModelFactory(private val loginRepository: LoginRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(loginRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(loginRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(loginRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(loginRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(loginRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): LoginViewModelFactory {
            if (INSTANCE == null) {
                synchronized(LoginViewModelFactory::class.java) {
                    INSTANCE = LoginViewModelFactory(Injection.provideLoginRepository(context))
                }
            }
            return  INSTANCE as LoginViewModelFactory
        }
    }
}