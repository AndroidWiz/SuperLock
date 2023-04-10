package com.sk.superlock.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sk.superlock.R
import com.sk.superlock.data.dao.UserDao
import com.sk.superlock.data.database.UserDatabase
import com.sk.superlock.data.model.User
import com.sk.superlock.databinding.ActivityRegisterBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import java.io.IOException
import java.util.*

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var db: UserDatabase
    private lateinit var userDao: UserDao
    private var profileImageUri: Uri? = null
    private var mProfileImageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = UserDatabase.getDatabase(this)
        userDao = db.userDao()

        setUpToolbar(binding.toolbarRegisterActivity)

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
            registerUser()
            db.close()
        }
    }

    //checking validations of text input
    private fun validateRegisterDetails(): Boolean {
        return when {
            profileImageUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_profile_image), true)
                false
            }
            TextUtils.isEmpty(binding.etUsername.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_full_name), true)
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
        // checking validations
        if (validateRegisterDetails()) {

            val userName: String = binding.etUsername.text.toString().trim { it <= ' ' }
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            val formattedUserName = userName.replace(" ", "-")
            mProfileImageUrl = String.format(
                "%s-%s.%s",
                formattedUserName,
                System.currentTimeMillis(),
                getFileExtension(profileImageUri)
            )
            val imageUrl = mProfileImageUrl

            val user = User(
                profilePicture = imageUrl,
                userName = userName,
                email = email,
                password = password
            )

            // inserting user into database
            Thread {
                userDao.insertUser(user)
                Log.d("registeredUser", "User: $user")

                // get file path to profile picture
                val imagePath = profileImageUri?.let { getDataColumn(this, it, null, null) }
                Log.d("savedImagePath", imagePath.toString())
//                /storage/emulated/0/DCIM/Camera/IMG_20230411_025525_728.jpg


                runOnUiThread {
                    registrationSuccessful()
                }
            }.start()

        }
    }

    // get file path from URI
    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let { context.contentResolver.query(it, projection, selection, selectionArgs, null) }
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    // registration successful
    private fun registrationSuccessful() {
        showErrorSnackBar(resources.getString(R.string.registry_successful), false)
        // redirect to login
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
    }

    // image chooser
    private fun imageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        @Suppress("DEPRECATION")
        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    // image file extension
    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
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
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        this@RegisterActivity,
                        "Image selection Failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Image Request Cancelled", "Image selection cancelled by user.")
        }
    }

}