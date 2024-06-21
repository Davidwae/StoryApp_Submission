package com.waekaizo.storyapp_submission.data.local

data class UserModel (
    val name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean = false
)