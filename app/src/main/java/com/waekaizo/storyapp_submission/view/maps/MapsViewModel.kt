package com.waekaizo.storyapp_submission.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waekaizo.storyapp_submission.data.LoginRepository
import com.waekaizo.storyapp_submission.data.api.ListStoryItem

class MapsViewModel(private val loginRepository: LoginRepository): ViewModel() {
    fun getAllStoriesLocation() = loginRepository.getAllStories()

    val storyPager: LiveData<PagingData<ListStoryItem>> =
        loginRepository.getAllStoriesPager().cachedIn(viewModelScope)
}