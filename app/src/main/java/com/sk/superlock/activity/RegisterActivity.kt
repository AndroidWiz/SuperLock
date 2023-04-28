package com.sk.superlock.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sk.superlock.R
import com.sk.superlock.data.model.TokenResponse
import com.sk.superlock.data.model.User
import com.sk.superlock.data.services.ApiClient
import com.sk.superlock.data.services.ApiInterface
import com.sk.superlock.databinding.ActivityRegisterBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.GlideLoader
import com.sk.superlock.util.PrefManager
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

//    private var mProfileImageUrl: String = ""
    private var mProfileImageUrl: File? = null
    private lateinit var apiInterface: ApiInterface

    companion object {
        val TAG = "RegisterActivity"
        var profileImageUri: Uri? = null
        var mSavedPathUri: Uri? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarRegisterActivity)

        apiInterface = ApiClient.getClient(this@RegisterActivity).create(ApiInterface::class.java)

        // image chooser
        binding.ivUploadUserImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                imageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        // button login
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        // button continue
        binding.btnContinue.setOnClickListener {
            if(validateRegisterDetails()){
                registerUser()
            }
        }
    }

    //checking validations of text input
    private fun validateRegisterDetails(): Boolean {
        return when {
            profileImageUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_profile_image), true)
                false
            }
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(binding.etRepeatPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_repeat_password),
                    true
                )
                false
            }
            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etRepeatPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_repeat_password_mismatch),
                    true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    // register user
    private fun registerUser() {
        val firstName: String = binding.etFirstName.text.toString().trim { it <= ' ' }
        val lastName: String = binding.etLastName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        val user = User(
//            id = null,
            name = firstName,
            lastname = lastName,
            email = email,
            password = password,
//            files = mProfileImageUrl
            files = null
        )

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", user.name)
            .addFormDataPart("lastname", user.lastname)
            .addFormDataPart("email", user.email)
            .addFormDataPart("password", user.password)

        if (user.files != null) {
            requestBody.addFormDataPart(
                "files",
                user.files!!.name,
                RequestBody.create(MediaType.parse("image/*"), user.files!!)
            )
        }

        // complete the part
        apiInterface.createUser(requestBody.build())
            .enqueue(object: Callback<TokenResponse>{
                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: Response<TokenResponse>
                ) {
                    if(response.isSuccessful){
                        val tokenResponse: TokenResponse? = response.body()
                        val accessToken = tokenResponse?.payload?.data?.accessToken
                        val refreshToken = tokenResponse?.payload?.data?.refreshToken

                        // save tokens to sharedPrefs
                        if(!accessToken.isNullOrEmpty()){
                            PrefManager(this@RegisterActivity).setAccessToken(accessToken)
                            Log.d(TAG, "accessToken: $accessToken")
                        }
                        if(!refreshToken.isNullOrEmpty()){
                            PrefManager(this@RegisterActivity).setRefreshToken(refreshToken)
                            Log.d(TAG, "refreshToken: $refreshToken")
                        }

                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    }else{
                        val errorMsg = response.errorBody()?.string()
                        val statusCode = response.code()
                        Log.e(TAG, "Something went wrong. Error message: $errorMsg. Status code: $statusCode")
                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.registration_unsuccessful), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, resources.getString(R.string.registration_unsuccessful), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "RegistrationFailed", t)
                }

            })
    }


    // image chooser
    private fun imageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        @Suppress("DEPRECATION")
        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    // request read storage permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // if permission granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageChooser()
            } else {
                Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                profileImageUri = data.data!!
                try {
                    GlideLoader(this).loadUserPicture(profileImageUri!!, iv_upload_user_image)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@RegisterActivity, resources.getString(R.string.img_selection_failed), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Image Request Cancelled", "Image selection cancelled by user.")
        }
    }

}