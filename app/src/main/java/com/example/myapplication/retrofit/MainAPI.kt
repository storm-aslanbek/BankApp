package com.example.myapplication.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MainAPI {
    @GET("items/")
    suspend fun getUserById(): UserData

    @POST("register/")
    suspend fun register(@Body regRequest: PostUserData): UserData

    @POST("auth/")
    suspend fun auth(@Body authRequest: AuthPost): Response<AuthResponse>
}