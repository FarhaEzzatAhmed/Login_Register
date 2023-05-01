package com.example.loginandrgister.utils

import com.example.loginandrgister.data.AuthResponse
import com.example.loginandrgister.data.LoginBody
import com.example.loginandrgister.data.RegisterBody
import com.example.loginandrgister.data.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {

    @POST("api/v1/auth/signup")
    suspend fun registerUser(@Body body:RegisterBody):Response<RegisterResponse>


    @POST("api/v1/auth/signin")
    suspend fun loginUser(@Body body: LoginBody):Response<AuthResponse>
}