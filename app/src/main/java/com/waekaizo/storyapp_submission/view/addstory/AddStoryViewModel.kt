package com.waekaizo.storyapp_submission.view.addstory

import androidx.lifecycle.ViewModel
import com.waekaizo.storyapp_submission.data.LoginRepository
import java.io.File

class AddStoryViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    fun uploadStory(imageFile: File, description: String) = loginRepository.uploadStory(imageFile, description)
}