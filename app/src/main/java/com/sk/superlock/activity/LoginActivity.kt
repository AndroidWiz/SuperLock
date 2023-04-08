package com.sk.superlock.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.data.dao.UserDao
import com.sk.superlock.data.database.UserDatabase
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.CameraLoginFragment
import com.sk.superlock.fragment.LoginUsernameFragment
import com.sk.superlock.util.Constants
import kotlinx.android.synthetic.main.fragment_login_username.*

enum class LoginMode {
    EMAIL_PASSWORD,
    CAMERA
}

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginMode: LoginMode? = null
    private lateinit var db: UserDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = UserDatabase.getDatabase(this)
        userDao = db.userDao()

        // default fragment
        showLoginFragment(LoginUsernameFragment())

        // radio button to show login by modes
        binding.rgLoginType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.login_by_email -> {
                    showLoginFragment(LoginUsernameFragment())
                }
                R.id.login_by_camera -> {
                    showLoginFragment(CameraLoginFragment())
                }
            }
        }

        // button login
        binding.btnLogin.setOnClickListener {
            when (loginMode) {
                LoginMode.EMAIL_PASSWORD -> {
                    val email: String = et_email.text.toString().trim()
                    val password: String = et_password.text.toString().trim()

                    // check if text views are empty
                    if(TextUtils.isEmpty(email)){
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    if (TextUtils.isEmpty(password)){
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                    }

                    Thread {
                        // retrieve the user from the database
                        val user = userDao.getUserByEmailAndPassword(email, password)

                        runOnUiThread {
                            // check if the user exists and the password is correct
                            if (user == null) {
                                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            } else {
                                // save the user ID to shared preferences
                                val prefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
                                prefs.edit().putLong(Constants.USER_ID, user.id!!).apply()

                                // start the main activity
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }.start()
                }
                LoginMode.CAMERA
                -> {

                }
                else -> {}
            }

            /*startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()*/
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

        // set login mode based on currently visible fragment
        loginMode = when (fragment) {
            is LoginUsernameFragment -> LoginMode.EMAIL_PASSWORD
            is CameraLoginFragment -> LoginMode.CAMERA
            else -> null
        }
    }


}