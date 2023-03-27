package com.sk.superlock.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class CustomRadioButton(context: Context, attrs: AttributeSet) : AppCompatRadioButton(context, attrs) {
    // initialising the class
    init{
        // call the function to apply to font to the components
        applyFont()
    }

    /**
     * Applies a font to a Radio Button.
     */
    private fun applyFont() {
        // this is used to get the file from the assets folder and set it to the title textView.
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "montserrat_bold.ttf")
        typeface = typeFace
    }
}