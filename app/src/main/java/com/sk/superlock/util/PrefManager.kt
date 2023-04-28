package com.sk.superlock.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sk.superlock.data.model.User
import org.json.JSONObject

class PrefManager(context: Context) {

    companion object {
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
    }

    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val gson: Gson = Gson()

    init {
        pref = context.getSharedPreferences(Constants.APP_PREFERENCES, 0)
        editor = pref.edit()
    }

    fun setAccessTokenAndRefreshToken(jsonResponse: String) {
        val jsonObject = JSONObject(jsonResponse)
        val accessToken = jsonObject.getJSONObject("payload")
            .getJSONObject("data")
            .getString("accessToken")
        val refreshToken = jsonObject.getJSONObject("payload")
            .getJSONObject("data")
            .getString("refreshToken")

        editor.putString(ACCESS_TOKEN, accessToken)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.commit()
    }

    fun setRefreshToken(refreshToken: String) {
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.commit()
    }

    fun hasRefreshToken(): Boolean {
        val token = pref.getString(REFRESH_TOKEN, "")
        return token!!.isNotBlank()
    }

    fun getRefreshToken(): String {
        return pref.getString(REFRESH_TOKEN, "")!!
    }

    fun setAccessToken(accessToken: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.commit()
    }

    fun hasAccessToken(): Boolean {
        val token = pref.getString(ACCESS_TOKEN, "")
        return token!!.isNotBlank()
    }

    fun getAccessToken(): String {
        return pref.getString(ACCESS_TOKEN, "")!!
    }

    fun clearSession() {
        editor.clear()
        editor.commit()
    }

    fun setUser(user: User) {
        editor.putString(Constants.USER, gson.toJson(user))
        editor.commit()
    }

    fun getUser(): User {
        val type = object : TypeToken<User>() {}.type
        return gson.fromJson(pref.getString(Constants.USER, "{}"), type)
    }
}