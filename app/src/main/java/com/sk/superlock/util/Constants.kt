package com.sk.superlock.util

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {

    const val APP_PREFERENCES: String = "KeyFacePrefs"
    const val ADDED_APP_LIST: String = "added_app_list"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val USERS: String = "users"
    const val USER_ID: String = "user_id"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val LOGGED_IN_USER_ID: String = "logged_in_user_id"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOGGED_IN_USER_EMAIL: String = "logged_in_user_email"
    const val LOGGED_IN_USER_IMAGE: String = "logged_in_user_image"
    const val OPEN_CAMERA_PERMISSION_CODE = 3

    const val USER_IMAGES_PATH: String = "user-images/"

    // image file extension
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}