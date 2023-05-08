package com.sk.superlock.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sk.superlock.data.model.Intruder
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

    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun saveBool(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBool(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String): String {
        return pref.getString(key, "")!!
    }

    fun isPinSet(): Boolean {
        return pref.contains(Constants.PIN)
    }

    fun setPin(pin: String) {
        editor.putString(Constants.PIN, pin)
        editor.commit()
    }

    fun verifyPin(pin: String): Boolean {
        val savedPin = pref.getString(Constants.PIN, null)
        return savedPin == pin
    }

    fun resetPin() {
        editor.remove(Constants.PIN)
        editor.commit()
    }

    fun isAppLocked(packageName: String): Boolean {
        val lockedApps = pref.getStringSet("lockedApps", emptySet()) ?: emptySet()
        return lockedApps.contains(packageName)
    }

    fun addLockedApp(packageName: String) {
        val lockedApps = pref.getStringSet("lockedApps", mutableSetOf()) ?: mutableSetOf()
        lockedApps.add(packageName)
        editor.putStringSet("lockedApps", lockedApps)
        editor.commit()
    }

    fun removeLockedApp(packageName: String) {
        val lockedApps = pref.getStringSet("lockedApps", mutableSetOf()) ?: mutableSetOf()
        lockedApps.remove(packageName)
        editor.putStringSet("lockedApps", lockedApps)
        editor.commit()
    }

    //    fun saveIntruder(intruder: Intruder) {
//        editor.putString(Constants.INTRUDER, gson.toJson(intruder))
//        editor.commit()
//    }
    fun saveIntruder(intruder: Intruder) {
        val intruders = getIntruderList().apply {
            add(intruder)
        }
        val json = gson.toJson(intruders)
        editor.putString(Constants.INTRUDER, json)
        editor.commit()
    }

    fun updateIntruderList(intruders: ArrayList<Intruder>) {
        val json = gson.toJson(intruders)
        editor.putString(Constants.INTRUDER, json)
        editor.commit()
    }

    fun getIntruderList(): ArrayList<Intruder> {
        val json = pref.getString(Constants.INTRUDER, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<ArrayList<Intruder>>() {}.type)
        } else {
            arrayListOf()
        }
    }
}