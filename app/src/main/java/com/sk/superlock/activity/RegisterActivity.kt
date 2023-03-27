package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import com.sk.superlock.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarRegisterActivity)

        // button login
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        // button continue
        binding.btnContinue.setOnClickListener {
            // TODO: check validations before continue
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }
}