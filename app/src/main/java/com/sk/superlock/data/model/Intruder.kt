package com.sk.superlock.data.model

import android.graphics.drawable.Drawable

data class Intruder(
    val intruderImage: Drawable,
    val appName: String = "",
    val appImage: Drawable,
    val time: String = ""
)
