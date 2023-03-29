package com.sk.superlock.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.sk.superlock.BuildConfig
import com.sk.superlock.R
import com.sk.superlock.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {

    private lateinit var binding: FragmentAboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpTexts()

        return view
    }

    private fun setUpTexts() {
        binding.versionName.text = (buildString {
            append("V")
            append(BuildConfig.VERSION_NAME)
        })

        val webView = binding.appDescription
        webView.settings.defaultFontSize = 14
        val mimeType = "text/html;charset=UTF-8"
        val encoding = "utf-8"
        val htmlText = resources.getString(R.string.app_description)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        val text = ("<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/montserrat_regular.ttf\")}body{font-family: MyFont;color: #D1D1D1;line-height:1.6}"
                + "</style></head>"
                + "<body>"
                + htmlText
                ) + "</body></html>"
        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null)

    }

}