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
import com.sk.superlock.data.dao.UserDao
import com.sk.superlock.data.database.UserDatabase
import com.sk.superlock.data.model.User
import com.sk.superlock.databinding.ActivityRegisterBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.GlideLoader
import java.io.IOException

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    //    private val db = UserDatabase.getDatabase(applicationContext)
//    private val userDao = db.userDao()
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
//            val imageChooserIntent = Intent()
//            imageChooserIntent.type = "image/*"
//            imageChooserIntent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(
//                Intent.createChooser(imageChooserIntent, "Select Profile Image"),
//                1
//            )
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                imageChooser()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
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
//                showErrorSnackBar(resources.getString(R.string.registry_successful), false)
                true
            }
        }
    }

    // register user
    private fun registerUser() {
        // checking validations
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

//            val imagePath: String = getRealPathFromURI(profileImageUri!!)
            val imagePath: String = mProfileImageUrl
            val userName: String = binding.etUsername.text.toString().trim { it <= ' ' }
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            val user = User(
                profilePicture = imagePath,
                userName = userName,
                email = email,
                password = password
            )

            // inserting user into database
            userDao.insertUser(user)

            hideProgressDialog()

            showErrorSnackBar(resources.getString(R.string.registry_successful), false)

            // redirect to login
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    // image chooser
    private fun imageChooser(){
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
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            // if permission granted
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                imageChooser()
            }else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        /*if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val imagePath = getRealPathFromURI(imageUri)
            // use image path in your registration logic
        }*/
        if(resultCode == Activity.RESULT_OK){
            if(resultCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null){
                    profileImageUri = data.data!!
                    try{
                        GlideLoader(this).loadUserPicture(profileImageUri!!, binding.ivUploadUserImage)
                    }catch (e: IOException){
                       e.printStackTrace()
                       Toast.makeText(this@RegisterActivity, "Image selection Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("Image Request Cancelled", "Image selection cancelled by user.")
        }
    }

    // get image path
    /*private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            cursor.close()
            return path
        }
        return ""
    }*/


}