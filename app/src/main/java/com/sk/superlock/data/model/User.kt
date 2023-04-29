package com.sk.superlock.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val lastname: String,
    val email: String,
    val imageURL: String,
    val imageName: String,
    val roles: List<String>
) : Parcelable


data class CreateUserResponse(
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
