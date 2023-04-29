package com.sk.superlock.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Credentials(
    val email: String?,
    val password: String?
) : Parcelable
