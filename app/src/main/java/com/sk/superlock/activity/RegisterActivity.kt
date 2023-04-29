package com.sk.superlock.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sk.superlock.R
import com.sk.superlock.data.model.UserResponse
import com.sk.superlock.data.services.ApiClient
import com.sk.superlock.data.services.ApiInterface
import com.sk.superlock.databinding.ActivityRegisterBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var mProfileImageUrl: String = ""
    private lateinit var apiInterface: ApiInterface
    companion object {
        val TAG = "RegisterActivity"
        var profileImageUri: Uri? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarRegisterActivity)

        apiInterface = ApiClient.getClient(this@RegisterActivity).create(ApiInterface::class.java)

        // check if camera permission is available
        binding.ivUploadUserImage.setOnClickListener {
            // if permission is granted, open camera
            if (ContextCompat.checkSelfPermission(this@RegisterActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ) {
                imageChooser()
            } else {
            // ask permission if camera access not granted
                ActivityCompat.requestPermissions(this@RegisterActivity, arrayOf(Manifest.permission.CAMERA), Constants.OPEN_CAMERA_PERMISSION_CODE)
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
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_repeat_password), true
                )
                false
            }
            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etRepeatPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_repeat_password_mismatch), true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                true
            }
        }
    }

    // register user
    private fun registerUser() {
        val name: RequestBody = RequestBody.create(MediaType.parse("text/plain"), binding.etFirstName.text.toString().trim())
        val lastName: RequestBody = RequestBody.create(MediaType.parse("text/plain"), binding.etLastName.text.toString().trim())
        val email: RequestBody = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.text.toString().trim())
        val password: RequestBody = RequestBody.create(MediaType.parse("text/plain"), binding.etPassword.text.toString().trim())

        val profileImage: MultipartBody.Part? = if (profileImageUri != null) {
            val file = File(getRealPathFromUri(profileImageUri!!))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            MultipartBody.Part.createFormData("files", file.name, requestFile)
        } else {
            null
        }

        apiInterface.createUser(name = name, lastname = lastName, email = email, password = password, files = arrayListOf(profileImage))
            .enqueue(object: Callback<UserResponse>{
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if(response.isSuccessful){
                        val tokenResponse: UserResponse? = response.body()
                        Log.d(TAG, "createUserTokenResponse: $tokenResponse")

                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                        // registration successful, go to LoginActivity to let the user login
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    }else{
                        val errorMsg = response.errorBody()?.string()
                        val statusCode = response.code()
                        Log.e(TAG, "Something went wrong. Error message: $errorMsg. Status code: $statusCode")
                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.registration_unsuccessful), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, resources.getString(R.string.registration_unsuccessful), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "RegistrationFailed", t)
                }

            })
    }


    // image chooser
    private fun imageChooser() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, Constants.OPEN_CAMERA_PERMISSION_CODE)
        } else {
            Toast.makeText(this@RegisterActivity, resources.getString(R.string.camera_unavailable), Toast.LENGTH_SHORT).show()
        }
    }

    // request read storage permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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

        if (requestCode == Constants.OPEN_CAMERA_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                profileImageUri = getImageUri(this@RegisterActivity, imageBitmap)
//                binding.ivUploadUserImage.setImageURI(profileImageUri)
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

    // get image uri to show in the imageview
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "User Profile Image",
            null
        )
        return Uri.parse(path)
    }

    // get the real path from a content uri
    private fun getRealPathFromUri(uri: Uri): String {
        val filePath: String
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            filePath = uri.path.toString()
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }
}