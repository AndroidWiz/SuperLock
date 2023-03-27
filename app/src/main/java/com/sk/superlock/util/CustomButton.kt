package com.sk.superlock.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class CustomButton(context: Context, attributeSet: AttributeSet) : AppCompatButton(context, attributeSet) {
    // initialising the class
    init{
        // call the function to apply to font to the components
        applyFont()
    }

    private fun applyFont() {
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "montserrat_bold.ttf")
        typeface = typeFace
    }

}