package com.waekaizo.storyapp_submission.data.di

import android.content.Context
import com.waekaizo.storyapp_submission.data.LoginRepository
import com.waekaizo.storyapp_submission.data.api.ApiConfig
import com.waekaizo.storyapp_submission.data.database.StoryDatabase
import com.waekaizo.storyapp_submission.data.local.UserPreference
import com.waekaizo.storyapp_submission.data.local.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideLoginRepository(context: Context): LoginRepository {
        val database = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return LoginRepository.getInstance(apiService, pref, database)
    }
}