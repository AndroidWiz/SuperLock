package com.sk.superlock.data.services

import android.content.Context
import com.sk.superlock.util.PrefManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {

        private const val BASE_URL = "http://54.167.88.104"
        private var retrofit: Retrofit? = null

        fun getClient(context: Context): Retrofit {
            val client: OkHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(AccessTokenInterceptor(PrefManager(context).getAccessToken()))
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }
}