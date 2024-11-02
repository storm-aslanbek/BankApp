package com.example.myapplication.retrofit

data class AuthResponse(
    val last_name: String,
    val first_name: String,
    val phone_number: String,
    val password: String,
    val balance: Int
)
