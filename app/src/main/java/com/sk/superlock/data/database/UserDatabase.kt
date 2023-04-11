package com.sk.superlock.data.database

import android.content.Context
import com.sk.superlock.data.model.User

//@Database(entities = [User::class], version = 1, exportSchema = false)
//abstract class UserDatabase : RoomDatabase() {
//
//    abstract fun userDao(): UserDao
//
//    companion object {
//
//        @Volatile
//        private var INSTANCE: UserDatabase? = null
//
//        fun getDatabase(context: Context): UserDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    UserDatabase::class.java,
//                    "user_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
