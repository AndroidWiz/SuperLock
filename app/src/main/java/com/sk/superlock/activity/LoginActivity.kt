package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.auth0.android.jwt.JWT
import com.sk.superlock.R
import com.sk.superlock.data.model.Credentials
import com.sk.superlock.data.model.User
import com.sk.superlock.data.model.UserResponse
import com.sk.superlock.data.services.ApiClient
import com.sk.superlock.data.services.ApiInterface
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.LoginCameraFragment
import com.sk.superlock.fragment.LoginUsernameFragment
import com.sk.superlock.util.LoginMode
import com.sk.superlock.util.PrefManager
import kotlinx.android.synthetic.main.fragment_login_username.*
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
                    if (validateLoginDetails()) {

                        val email: String = et_email.text.toString().trim()
                        val password: String = et_password.text.toString().trim()

                        login(email, password)
                    }
                }
                LoginMode.CAMERA -> {
                    Toast.makeText(
                        this,
                        "Please use email and password to login",
                        Toast.LENGTH_SHORT
                    ).show()
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
    private fun login(email: String, password: String) {
        apiInterface.loginUser(Credentials(email, password))
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful && response.code() == 201) {
                        val tokenResponse: UserResponse? = response.body()
                        if (tokenResponse != null) {
                            val accessToken = tokenResponse.payload.data.accessToken
                            val refreshToken = tokenResponse.payload.data.refreshToken
                            // token stored in shared preferences
                            PrefManager(this@LoginActivity).setAccessToken(accessToken)
                            PrefManager(this@LoginActivity).setAccessToken(refreshToken)
                            Log.d(TAG, "accessToken: $accessToken, refreshToken: $refreshToken")

                            // decode user data
                            val user = decodeAccessToken(accessToken)
                            Log.d(TAG, "DecodedUser = $user")

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("login", "Login failed")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    Log.d("login", "Login failed", t)
                }
            })
    }

    // jwt decoder
    fun decodeAccessToken(accessToken: String): User? {
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

    /*  fun decodeAccessToken(userResponse: UserResponse): User? {
          val accessToken = userResponse.payload.data.accessToken
          if (accessToken == null) {
              Log.e(TAG, "Access token is null")
              return null
          }
          val jwt = JWT(accessToken)
          val claims = Gson().fromJson(jwt.getClaim("payload").asString(), JsonObject::class.java)

          val id = claims.getAsJsonObject("data").get("id").asInt
          val name = claims.getAsJsonObject("data").get("name").asString
          val lastname = claims.getAsJsonObject("data").get("lastname").asString
          val email = claims.getAsJsonObject("data").get("email").asString
          val imageURL = claims.getAsJsonObject("data").get("imageURL").asString
          val roles = claims.getAsJsonObject("data").getAsJsonArray("roles").map { it.asString }

          return User(
              id = id,
              name = name,
              lastname = lastname,
              email = email,
              imageURL = imageURL,
              roles = roles
          )
      }*/

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
            is LoginCameraFragment -> LoginMode.CAMERA
            else -> null
        }
    }


}