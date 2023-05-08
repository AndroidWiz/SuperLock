package com.sk.superlock.data.model

import android.graphics.drawable.Drawable

data class Applications(
    val appName: String,
    val appPackageName: String,
    val appIcon: Drawable,
    var isLocked: Boolean = false
)
