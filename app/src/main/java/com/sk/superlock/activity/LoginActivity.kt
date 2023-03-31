package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.LoginCameraFragment
import com.sk.superlock.fragment.LoginUsernameFragment

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default fragment
        showLoginFragment(LoginUsernameFragment())

        // radio button to show login by modes
        binding.rgLoginType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.login_by_email -> {
                    showLoginFragment(LoginUsernameFragment())
                }
                R.id.login_by_camera -> {
                    showLoginFragment(LoginCameraFragment())
                }
            }
        }

        // button login
        binding.btnLogin.setOnClickListener {
            // TODO: check from which radio button
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            finish()
        }

        // forgot password button
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        // register button
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

    }

    // show login fragment view
    private fun showLoginFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.loginFragmentHost.id, fragment)
            .commit()
    }


}