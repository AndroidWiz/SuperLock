package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.R
import com.sk.superlock.model.SliderData
import com.sk.superlock.util.CustomTextView
import com.sk.superlock.util.CustomTextViewBold

class SliderAdapter(private val context: Context, private val sliderList: MutableList<SliderData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_intro_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sliderList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sliderViewHolder = holder as SliderViewHolder
        val sliderData = sliderList[position]

        sliderViewHolder.tvSliderHeading.text = sliderData.slideTitle
        sliderViewHolder.tvSliderDesc.text = sliderData.slideDescription
        sliderViewHolder.imgSlider.setImageDrawable(sliderData.slideImage)
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSlider: ImageView = itemView.findViewById(R.id.iv_slider)
        val tvSliderHeading: CustomTextViewBold = itemView.findViewById(R.id.tv_slider_title)
        val tvSliderDesc: CustomTextView = itemView.findViewById(R.id.tv_slider_description)
    }
}

