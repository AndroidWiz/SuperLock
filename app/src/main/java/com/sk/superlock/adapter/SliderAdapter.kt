package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.sk.superlock.R
import com.sk.superlock.model.SliderData

class SliderAdapter(private val context: Context, private val sliderList: ArrayList<SliderData>) : PagerAdapter() {

    override fun getCount(): Int {
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = layoutInflater.inflate(R.layout.item_intro_slider, container, false)

        val imgSlider : ImageView = view.findViewById(R.id.iv_slider)
        val tvSliderHeading: TextView = view.findViewById(R.id.tv_slider_title)
        val tvSliderDesc: TextView = view.findViewById(R.id.tv_slider_description)

        val sliderData : SliderData = sliderList[position]
        tvSliderHeading.text = sliderData.slideTitle
        tvSliderDesc.text = sliderData.slideDescription
//        imgSlider.setImageResource(sliderData.slideImage)
        imgSlider.setImageDrawable(sliderData.slideImage)


        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}