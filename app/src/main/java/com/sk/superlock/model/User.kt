package com.sk.superlock.model

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val images: MutableList<String> = mutableListOf(),
    val locations: MutableList<Location> = mutableListOf()
)