package com.sk.superlock.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.sk.superlock.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        // checking device OS and setting flags to full screen window
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }*/

       /* Handler().postDelayed({
            // check current logged in user
            val currentUserId = Firestore().getCurrentUserId()

            if(currentUserId.isNotEmpty()){
                // launch main activity
                startActivity(Intent (this@SplashActivity, MainActivity::class.java))
            }else{
                // launch login activity
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        },
        2500)*/

        Handler().postDelayed({
//            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
        }, 2500)
    }
}