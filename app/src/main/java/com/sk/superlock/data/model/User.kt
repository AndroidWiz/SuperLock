package com.sk.superlock.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val userName: String?,
    val email: String?,
    val password: String?,
    val profilePicture: String?,
//    val images: ArrayList<String>? = null,
//    val locations: ArrayList<Location>? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
