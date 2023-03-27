package com.sk.superlock.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTextView(context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {
    // initialising the class
    init{
        // call the function to apply to font to the components
        applyFont()
    }

    private fun applyFont(){
        // this is used to get the file from the assets folder and set it to the title textView.
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "montserrat_regular.ttf")
        typeface = typeFace
    }
}