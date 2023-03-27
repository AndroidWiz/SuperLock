package com.sk.superlock.activity

import android.os.Bundle
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarForgotPassActivity)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white)
    }
}