package com.sk.superlock.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val userName: String = "",
    val email: String = "",
    var profilePicture: String = "",
) : Parcelable
