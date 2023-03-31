package com.sk.superlock.activity

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.sk.superlock.R
import com.sk.superlock.adapter.SliderAdapter
import com.sk.superlock.databinding.ActivityIntroBinding
import com.sk.superlock.model.SliderData
import com.sk.superlock.util.CustomButton
import com.sk.superlock.util.CustomTextViewBold

@Suppress("DEPRECATION")
class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewpager: ViewPager
    private lateinit var sliderAdapter : SliderAdapter
    private lateinit var sliderList: ArrayList<SliderData>
    private lateinit var btnSkip : CustomButton
    private lateinit var indicatorSlider1 : CustomTextViewBold
    private lateinit var indicatorSlider2 : CustomTextViewBold
    private lateinit var indicatorSlider3 : CustomTextViewBold

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewpager = binding.introViewPager
        btnSkip = binding.btnSkipIntro
        indicatorSlider1 = binding.tvSlide1
        indicatorSlider2 = binding.tvSlide2
        indicatorSlider3 = binding.tvSlide3

        // button skip takes to MainActivity
        btnSkip.setOnClickListener {
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        }

        // add dummy data
        sliderList = ArrayList()
//        sliderList.add(SliderData("Key Face PIN", "Set your PIN", R.drawable.ic_password_type))
//        sliderList.add(SliderData("Key Face face-detection", "Set your app lock using face-ID", R.drawable.ic_face))
//        sliderList.add(SliderData("Key Face geolocation", "Set your desired parameter to unlock your apps", R.drawable.ic_location))
        sliderList.add(SliderData("Key Face PIN", "Set your PIN",
            ContextCompat.getDrawable(this@IntroActivity, R.drawable.ic_password_type)?.apply { setColorFilter(ContextCompat.getColor(this@IntroActivity, R.color.white), PorterDuff.Mode.SRC_IN) }!!
        ))
        sliderList.add(SliderData("Key Face face-detection", "Set your app lock using face-ID",
            ContextCompat.getDrawable(this@IntroActivity, R.drawable.ic_face)?.apply { setColorFilter(ContextCompat.getColor(this@IntroActivity, R.color.white), PorterDuff.Mode.SRC_IN) }!!
        ))
        sliderList.add(SliderData("Key Face geolocation", "Set your desired parameter to unlock your apps",
            ContextCompat.getDrawable(this@IntroActivity, R.drawable.ic_location)?.apply { setColorFilter(ContextCompat.getColor(this@IntroActivity, R.color.white), PorterDuff.Mode.SRC_IN) }!!
        ))

        // adapter class
        sliderAdapter = SliderAdapter(this@IntroActivity, sliderList)
        viewpager.adapter = sliderAdapter
        viewpager.addOnPageChangeListener(viewListener)
    }

    private var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            // checking position and updating text view text color.
            when (position) {
                0 -> {
                    indicatorSlider1.setTextColor(resources.getColor(R.color.white))
                    indicatorSlider2.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    indicatorSlider3.setTextColor(resources.getColor(R.color.colorPrimaryDark))

                }
                1 -> {
                    indicatorSlider1.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    indicatorSlider2.setTextColor(resources.getColor(R.color.white))
                    indicatorSlider3.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                }
                else -> {
                    indicatorSlider1.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    indicatorSlider2.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    indicatorSlider3.setTextColor(resources.getColor(R.color.white))
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}

    }

    override fun onResume() {
        indicatorSlider1.setTextColor(resources.getColor(R.color.white))
        indicatorSlider2.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        indicatorSlider3.setTextColor(resources.getColor(R.color.colorPrimaryDark))

        super.onResume()
    }
}