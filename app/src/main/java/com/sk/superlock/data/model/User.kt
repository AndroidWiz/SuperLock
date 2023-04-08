package com.sk.superlock.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "users")
@TypeConverters
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userName: String?,
    val email: String?,
    val password: String?,
    val profilePicture: String?,
//    val images: ArrayList<String> = ArrayList(),
//    val locations: ArrayList<Location> = ArrayList()
)