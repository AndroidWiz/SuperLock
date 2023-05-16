package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.auth0.android.jwt.JWT
import com.sk.superlock.R
import com.sk.superlock.data.model.Credentials
import com.sk.superlock.data.model.User
import com.sk.superlock.data.model.UserResponse
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.services.ApiClient
import com.sk.superlock.services.ApiInterface
import com.sk.superlock.util.LoginMode
import com.sk.superlock.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginMode: LoginMode? = null
    private val userImagesList = CopyOnWriteArrayList<File>()
    private lateinit var apiInterface: ApiInterface

    companion object {
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = ApiClient.getClient(this@LoginActivity).create(ApiInterface::class.java)

        // button login
        binding.btnLogin.setOnClickListener {
            if (validateLoginDetails()) {
                val email: String = binding.etEmail.text.toString().trim()
                val password: String = binding.etPassword.text.toString().trim()

                login(email, password)
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
    private fun login(email: String, password: String) {
        apiInterface.loginUser(Credentials(email, password))
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
//                    if (response.isSuccessful && response.code() == 201) {
                    if (response.isSuccessful) {
                        val tokenResponse: UserResponse? = response.body()
                        if (tokenResponse != null) {
                            val accessToken = tokenResponse.payload.data.accessToken
                            val refreshToken = tokenResponse.payload.data.refreshToken
                            // token stored in shared preferences
                            PrefManager(this@LoginActivity).setAccessToken(accessToken)
                            PrefManager(this@LoginActivity).setRefreshToken(refreshToken)
                            Log.d(TAG, "accessToken: $accessToken, refreshToken: $refreshToken")

                            // decode user data and save to sharedPrefs
                            val user = decodeAccessToken(accessToken)
                            PrefManager(this@LoginActivity).setUser(user)
                            Log.d(TAG, "DecodedUser = $user")

                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                            startActivity(Intent(this@LoginActivity, ConfigurationActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, resources.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Login failed")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Login failed", t)
                }
            })
    }

    // jwt decoder
    fun decodeAccessToken(accessToken: String): User {
        val jwt = JWT(accessToken)

        val id = jwt.getClaim("id").asInt()
        val name = jwt.getClaim("name").asString()
        val lastname = jwt.getClaim("lastname").asString()
        val email = jwt.getClaim("email").asString()
        val imageURL = jwt.getClaim("imageURL").asString()
        val roles = jwt.getClaim("roles").asList(String::class.java)

        return User(
            id = id!!,
            name = name!!,
            lastname = lastname!!,
            email = email!!,
            imageURL = imageURL!!,
            roles = roles
        )
    }

    // validate login details
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                // valid details process login
                true
            }
        }
    }

}