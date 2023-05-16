package com.sk.superlock.activity

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.sk.superlock.R
import com.sk.superlock.adapter.SliderAdapter
import com.sk.superlock.data.model.SliderData
import com.sk.superlock.databinding.ActivityIntroBinding
import com.sk.superlock.util.CustomTextViewBold

@Suppress("DEPRECATION")
class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewpager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private val sliderList = mutableListOf<SliderData>()
    private lateinit var indicatorTextViews: List<CustomTextViewBold>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewpager = binding.introViewPager
        indicatorTextViews = listOf(binding.tvSlide1, binding.tvSlide2, binding.tvSlide3)

        // button skip takes to LoginActivity
        binding.btnSkipIntro.setOnClickListener {
            startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
        }

        // add dummy data
        sliderList.addAll(
            mutableListOf(
                SliderData(
                    "Key Face PIN",
                    "Set your PIN",
                    ContextCompat.getDrawable(this@IntroActivity, R.drawable.ic_password_type)
                        ?.apply {
                            setColorFilter(
                                ContextCompat.getColor(this@IntroActivity, R.color.white),
                                PorterDuff.Mode.SRC_IN
                            )
                        }!!
                ),
                SliderData(
                    "Key Face face-detection",
                    getString(R.string.set_up_FACE_ID),
                    ContextCompat.getDrawable(this@IntroActivity, R.drawable.ic_face)?.apply {
                        setColorFilter(
                            ContextCompat.getColor(this@IntroActivity, R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                    }!!
                ),
                SliderData(
                    "Key Face geolocation",
                    getString(R.string.set_location_unlock),
                    ContextCompat.getDrawable(this@IntroActivity, R.drawable.ic_location)?.apply {
                        setColorFilter(
                            ContextCompat.getColor(this@IntroActivity, R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                    }!!
                )
            )
        )


        // adapter class
        sliderAdapter = SliderAdapter(this@IntroActivity, sliderList)
        viewpager.adapter = sliderAdapter
        viewpager.registerOnPageChangeCallback(viewListener)
    }

    private var viewListener: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // checking position and updating text view text color.
                indicatorTextViews.forEachIndexed { index, textView ->
                    textView.setTextColor(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            if (position == index) R.color.white else R.color.colorPrimary
                        )
                    )
                }
            }
        }

    override fun onResume() {
        super.onResume()
        indicatorTextViews.forEachIndexed { index, textView ->
            textView.setTextColor(
                ContextCompat.getColor(
                    this@IntroActivity,
                    if (viewpager.currentItem == index) R.color.white else R.color.colorPrimary
                )
            )
        }
    }
}