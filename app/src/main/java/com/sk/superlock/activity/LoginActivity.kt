package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.data.services.ApiClient
import com.sk.superlock.data.services.ApiInterface
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.LoginCameraFragment
import com.sk.superlock.fragment.LoginUsernameFragment
import com.sk.superlock.util.LoginMode
import kotlinx.android.synthetic.main.fragment_login_username.*
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginMode: LoginMode? = null
    private val userImagesList = CopyOnWriteArrayList<File>()
    private lateinit var apiInterface: ApiInterface

    companion object {
        val TAG = "LoginActivity"
//        val userImagesList = listOf<File>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = ApiClient.getClient(this@LoginActivity).create(ApiInterface::class.java)

        // default fragment
        showLoginFragment(LoginUsernameFragment())

        // radio button to show login by modes
        binding.rgLoginType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.login_by_email -> {
                    showLoginFragment(LoginUsernameFragment())
                }
                R.id.login_by_camera -> {
//                    showLoginFragment(CameraLoginFragment())
                    showLoginFragment(LoginCameraFragment())
                }
            }
        }

        // button login
        binding.btnLogin.setOnClickListener {
            when (loginMode) {
                LoginMode.EMAIL_PASSWORD -> {
                    if(validateLoginDetails()){

                    val email: String = et_email.text.toString().trim()
                    val password: String = et_password.text.toString().trim()



                    }
                }
                LoginMode.CAMERA -> {
                    Toast.makeText(this, "Please use email and password to login", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
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

    // perform login


    // validate login details
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                // valid details process login
                true
            }
        }
    }


    // show login fragment view
    private fun showLoginFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.loginFragmentHost.id, fragment)
            .commit()

        // set login mode based on currently visible fragment
        loginMode = when (fragment) {
            is LoginUsernameFragment -> LoginMode.EMAIL_PASSWORD
//            is CameraLoginFragment -> LoginMode.CAMERA
            is LoginCameraFragment -> LoginMode.CAMERA
            else -> null
        }
    }


}