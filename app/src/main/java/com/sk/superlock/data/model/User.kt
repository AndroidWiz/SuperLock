package com.sk.superlock.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class User(
//    val id: String?,
    val name: String,
    val lastname: String,
    var email: String,
    var password: String,
//    var files: MultipartBody.Part? = null,// image type file
    var files: File?,// image type file
    var roles: Int = 2
) : Parcelable

//@Parcelize
//data class TokenResponse(
//    var accessToken: String?,
//    var refreshToken: String?
//) : Parcelable


data class TokenResponse(
    @SerializedName("traceId") val traceId: String,
    @SerializedName("payload") val payload: Payload
)

data class Payload(
    @SerializedName("data") val data: Data
)

data class Data(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)
