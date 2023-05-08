package com.sk.superlock.util

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {

    const val APP_PREFERENCES: String = "KeyFacePrefs"
    const val ADDED_APP_LIST: String = "added_app_list"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val OPEN_CAMERA_PERMISSION_CODE = 3
    const val PERMISSION_REQUEST_USAGE_ACCESS = 100
    const val PERMISSION_REQUEST_AUTO_START = 101
    const val USER: String = "user"
    const val AVAILABLE_APPS_SIZE: String = "available_apps_in_device"
    const val FINGERPRINT_SWITCH_STATE: String = "fingerprint_switch_state"
    const val PASSWORD_TYPE: String = "password_type"
    const val PIN: String = "pin"
    const val ARGS_IS_CONFIGURATION: String = "is_for_setting_pin"
    const val INTRUDER: String = "intruder"
    const val LOCKED_APPS: String = "locked_apps"


    // image file extension
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}