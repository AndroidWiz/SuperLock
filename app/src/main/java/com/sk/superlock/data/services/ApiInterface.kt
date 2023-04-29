package com.sk.superlock.data.services

import com.sk.superlock.data.model.Credentials
import com.sk.superlock.data.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    // register user
    @Headers("Content-Type:application/json")
    @Multipart
    @POST("/users")
    fun createUser(
        @Part("name") name: RequestBody,
        @Part("lastname") lastname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part files: MultipartBody.Part?,
        @Part("roles") roles: RequestBody,
    ): Call<UserResponse>

    // login
    @POST("/auth/login")
    fun loginUser(@Body credentials: Credentials): Call<UserResponse>
}

//@Part("files") files: MultipartBody.Part?,
//@Part("roles") roles: Int = 2,