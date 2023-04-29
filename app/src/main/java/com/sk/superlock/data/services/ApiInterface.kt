package com.sk.superlock.data.services

import com.sk.superlock.data.model.Credentials
import com.sk.superlock.data.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    // register user
    @Multipart
    @POST("/users")
    fun createUser(
        @Part("id") id: Int = 0,
        @Part("name") name: RequestBody,
        @Part("lastname") lastname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part files: List<MultipartBody.Part?>,
        @Part("roles") roles: Int = 2,
    ): Call<UserResponse>

    // login
    @POST("/auth/login")
    fun loginUser(@Body credentials: Credentials): Call<UserResponse>
}
