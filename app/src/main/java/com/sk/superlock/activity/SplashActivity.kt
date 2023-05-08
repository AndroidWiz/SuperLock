package com.sk.superlock.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.sk.superlock.databinding.ActivitySplashBinding
import com.sk.superlock.util.PrefManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            if (PrefManager(this@SplashActivity).hasAccessToken()) {
//                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                startActivity(Intent(this@SplashActivity, LockActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 2500)
    }
}