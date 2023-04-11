package com.sk.superlock.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//@Entity(tableName = "users")
//data class User(
//    val userName: String?,
//    val email: String?,
//    val password: String?,
//    val profilePicture: String?,
////    val images: ArrayList<String>? = null,
////    val locations: ArrayList<Location>? = null
//) {
//    @PrimaryKey(autoGenerate = true)
//    var id: Long? = null
//}

@Parcelize
data class User(
    val id: String = "",
    val userName: String = "",
    val email: String = "",
    var profilePicture: String = "",
) : Parcelable
