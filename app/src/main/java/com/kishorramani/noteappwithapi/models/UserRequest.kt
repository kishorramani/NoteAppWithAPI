package com.kishorramani.noteappwithapi.models

data class UserRequest(
    val email: String,
    val password: String,
    val username: String
)