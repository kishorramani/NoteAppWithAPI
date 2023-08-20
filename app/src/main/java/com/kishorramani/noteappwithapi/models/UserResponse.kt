package com.kishorramani.noteappwithapi.models

data class UserResponse(
    val token: String,
    val user: User
)