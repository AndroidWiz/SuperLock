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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.sk.superlock.R
import com.sk.superlock.data.Firestore
import com.sk.superlock.data.model.User
import com.sk.superlock.databinding.ActivityRegisterBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import java.io.IOException
import java.util.*


class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var mProfileImageUrl: String = ""

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

        FirebaseApp.initializeApp(this)

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
//            registerUser()
            detectFaceInImage()
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
        val userName: String = binding.etUsername.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    val user = User(
                        id = firebaseUser.uid,
                        userName = userName,
                        email = email,
                    )

                    Firestore().registerUser(this, user)
                }
            }

        if (profileImageUri != null) {
            Firestore().uploadImageToStorage(
                this,
                profileImageUri,
                Constants.USER_PROFILE_IMAGE
            )
        } else {
            addImageToUser()
        }
    }

    private fun detectFaceInImage() {
        // checking validations
        if (validateRegisterDetails()) {

            showProgressDialog("Please wait...")
            if (profileImageUri != null) {
                val image = FirebaseVisionImage.fromFilePath(this, profileImageUri!!)
                val options = FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
                    .setMinFaceSize(0.15f)
                    .build()

                val detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options)

                detector.detectInImage(image)
                    .addOnSuccessListener { face ->
                        if (face.size == 1) {
                            registerUser()
                        } else if (face.size > 1) {
                            hideProgressDialog()
                            showErrorSnackBar(resources.getString(R.string.more_than_1_face_detected), true)
                            Log.d(TAG, "More faces detected")
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(resources.getString(R.string.no_face_detected_try_again), true)
                            Log.d(TAG, "No faces detected")
                        }
                    }
                    .addOnFailureListener { e ->
                        hideProgressDialog()
                        Log.e(TAG, "Error detecting face", e)
                    }
            }
        }
    }

    fun imageUploadSuccess(imageUrl: String) {
        mProfileImageUrl = imageUrl
        addImageToUser()
    }

    fun userProfileUpdated() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.profile_updated_with_image), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun addImageToUser() {
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageUrl.isNotEmpty()) {
            userHashMap["profilePicture"] = mProfileImageUrl
        }

        Firestore().updateUserProfileData(this, userHashMap)
    }


    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
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