package com.example.myapplication.retrofit

import okhttp3.Request
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MainAPI {
    @GET("items/1")
    suspend fun getUserById(): UserData

    @POST("auth/")
    suspend fun auth(@Body authRequest: PostUserData): UserData
}