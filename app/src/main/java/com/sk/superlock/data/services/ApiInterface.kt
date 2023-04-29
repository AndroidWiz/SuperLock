package com.sk.superlock.data.services

import com.sk.superlock.data.model.CreateUserResponse
import okhttp3.MultipartBody
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
        @Part files: MultipartBody.Part?,
        @Part("roles") roles: RequestBody,
    ): Call<CreateUserResponse>
}

//@Part("files") files: MultipartBody.Part?,
//@Part("roles") roles: Int = 2,