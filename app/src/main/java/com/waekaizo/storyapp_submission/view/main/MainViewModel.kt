package com.waekaizo.storyapp_submission.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waekaizo.storyapp_submission.data.LoginRepository
import com.waekaizo.storyapp_submission.data.api.ListStoryItem
import com.waekaizo.storyapp_submission.data.local.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return loginRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            loginRepository.logout()
        }
    }

    val storyPager: LiveData<PagingData<ListStoryItem>> =
        loginRepository.getAllStoriesPager().cachedIn(viewModelScope)

}