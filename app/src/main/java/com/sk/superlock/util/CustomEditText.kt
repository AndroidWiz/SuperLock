package com.sk.superlock.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {

    // initialising the class
    init {
        // call the function to apply to font to the components
        applyFont()
    }

    private fun applyFont() {
        // this is used to get the file from the assets folder and set it to the title EditText.
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "montserrat_regular.ttf")
        typeface = typeFace
    }
}