package com.waekaizo.storyapp_submission.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.waekaizo.storyapp_submission.data.api.AddStoryResponse
import com.waekaizo.storyapp_submission.data.api.ApiConfig
import com.waekaizo.storyapp_submission.data.api.ApiService
import com.waekaizo.storyapp_submission.data.api.ErrorResponse
import com.waekaizo.storyapp_submission.data.api.ListStoryItem
import com.waekaizo.storyapp_submission.data.api.LoginResponse
import com.waekaizo.storyapp_submission.data.api.RegisterResponse
import com.waekaizo.storyapp_submission.data.database.StoryDatabase
import com.waekaizo.storyapp_submission.data.local.UserModel
import com.waekaizo.storyapp_submission.data.local.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class LoginRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
){
    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)
            val data = UserModel (
                name = response.loginResult.name,
                userId = response.loginResult.userId,
                token = response.loginResult.token,
                isLogin = true
            )
            userPreference.saveSession(data)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    fun getAllStories(): LiveData<ResultState<List<ListStoryItem>>> = liveData {
        emit(ResultState.Loading)
        try {
            val pref = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(pref.token)
            val responseStory = response.getStoriesWithLocation()
            val story = responseStory.listStory
            val listStory = story.map { data ->
                ListStoryItem(
                    data.photoUrl,
                    data.createdAt,
                    data.name,
                    data.description,
                    data.lon,
                    data.id,
                    data.lat
                )
            }
            emit(ResultState.Success(listStory))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(ResultState.Error(errorResponse?.message ?: " An error occured"))
        }
    }

    fun uploadStory(imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {
            val pref = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(pref.token)
            val successResponse = response.uploadStory(multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
            emit(errorResponse.message.let { ResultState.Error(it) })
        }
    }

    fun getAllStoriesPager(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, userPreference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): LoginRepository =
            instance ?: synchronized(this) {
                instance ?: LoginRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }

}