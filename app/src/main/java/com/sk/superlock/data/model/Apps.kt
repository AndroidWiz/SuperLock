package com.sk.superlock.data.model

import android.graphics.drawable.Drawable

data class Apps(
    val icon: Drawable,
    val name: String,
    val packageName: String
) {
    var isLocked: Boolean = false
}