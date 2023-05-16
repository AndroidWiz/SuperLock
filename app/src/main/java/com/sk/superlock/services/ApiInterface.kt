package com.sk.superlock.services

import com.sk.superlock.data.model.Credentials
import com.sk.superlock.data.model.FaceResponse
import com.sk.superlock.data.model.ResetResponse
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

    // forgot password
    @GET("/auth/getEmail/{email}")
    fun resetUserPassword(@Path("email") email: String): Call<ResetResponse>

    // compare image
    @Headers("Content-Type: application/json;charset=UTF-8")
    @Multipart
    @POST("/users/compareFace")
    fun compareFace(
        @Header("Authorization") accessToken: String,
        @Part file: MultipartBody.Part?
    ): Call<FaceResponse>
}
