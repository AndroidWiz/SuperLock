package com.sk.superlock.services

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AccessTokenInterceptor(private var accessToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = newRequestWithAccessToken(chain.request(), accessToken)
        return chain.proceed(request)
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}