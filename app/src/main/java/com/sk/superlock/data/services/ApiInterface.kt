package com.sk.superlock.data.services

import com.sk.superlock.data.model.TokenResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @Headers("Content-Type:application/json")
    @Multipart
    @POST("/users")
    fun createUser(
        @Part("name") name: RequestBody,
        @Part("lastname") lastname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("files") files: RequestBody,
        @Part("roles") roles: RequestBody,
    ): Call<TokenResponse>
//    fun createUser(@Body requestBody: RequestBody): Call<TokenResponse>
//    fun createUser(@Body user: User): Call<TokenResponse>
//    fun createUser(@Body user: User): Response<TokenResponse>
}