package com.sk.superlock.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sk.superlock.data.model.User
import java.util.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    fun getUserByEmailAndPassword(email: String, password: String): User?

//    @Query("SELECT * FROM users WHERE id = :id")
//    fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE profilePicture = :profilePicture")
    fun getUserByProfilePicture(profilePicture: String): User?

}