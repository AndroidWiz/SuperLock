package com.sk.superlock.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.sk.superlock.R
import com.sk.superlock.data.Firestore
import com.sk.superlock.data.model.User
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.CameraLoginFragment
import com.sk.superlock.fragment.LoginUsernameFragment
import kotlinx.android.synthetic.main.fragment_login_username.*
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

enum class LoginMode {
    EMAIL_PASSWORD,
    CAMERA
}

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginMode: LoginMode? = null

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
                    showLoginFragment(CameraLoginFragment())
//                    showLoginFragment(LoginCameraFragment())
                }
            }
        }

        // button login
        binding.btnLogin.setOnClickListener {
            when (loginMode) {
                LoginMode.EMAIL_PASSWORD -> {
                    showProgressDialog("Please wait")

                    val email: String = et_email.text.toString().trim()
                    val password: String = et_password.text.toString().trim()

                    // check if text views are empty
                    if(TextUtils.isEmpty(email)){
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    if (TextUtils.isEmpty(password)){
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                    }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Firestore().getUserDetails(this)
                            }else{
                                hideProgressDialog()
                                showErrorSnackBar(task.exception!!.message.toString(), true)
                            }
                        }
                }
                LoginMode.CAMERA -> {
                    Thread{
//                        val imageUri = CameraLoginFragment.savedUri
////                        val profilePicList = listOf(userDao.getUsersWithProfilePictures())
//                        val profilePicList = userDao.getUsersWithProfilePictures()
//                        val user = userDao.getUserByProfilePicture(imageUri.toString())
//
//                        runOnUiThread{
//                            if(user == null){
//                                Toast.makeText(this, "No matching user found", Toast.LENGTH_SHORT).show()
//                            }else if(matchImages(imageUri!!, profilePicList)){
//                                // save the user ID to shared preferences
//                                val prefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
//                                prefs.edit().putLong(Constants.USER_ID, user.id!!).apply()
//
//                                // start the main activity
//                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                                finish()
//                            }else{
//                                Toast.makeText(this, "No match found", Toast.LENGTH_SHORT).show()
//                            }
//                        }
                    }.start()
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

    // match images
    private fun matchImages(imageUri: Uri, profilePictureList: List<User>): Boolean {
        // Load the captured image from the imageUri
        val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        val capturedMat = Mat()
        Utils.bitmapToMat(imageBitmap, capturedMat)

        // Convert the captured image to grayscale
        val capturedMatGray = Mat()
        Imgproc.cvtColor(capturedMat, capturedMatGray, Imgproc.COLOR_RGBA2RGB)

        // Loop through the profilePictureList and try to match each image with the captured image
        for (user in profilePictureList) {
            if(user.profilePicture == null){
                continue
            }
            // Load the profile picture from the path
            val profilePictureMat = Imgcodecs.imread(user.profilePicture)

            // Convert the profile picture to grayscale
            val profilePictureMatGray = Mat()
            Imgproc.cvtColor(profilePictureMat, profilePictureMatGray, Imgproc.COLOR_BGR2GRAY)

            // Perform template matching
            val result = Mat()
            Imgproc.matchTemplate(capturedMatGray, profilePictureMatGray, result, Imgproc.TM_CCOEFF_NORMED)

            // Check if the match score is above a certain threshold
            val matchScore = Core.minMaxLoc(result).maxVal
            if (matchScore > 0.8) {
                return true
            }
        }
        return false
    }


    // login success
    fun userLoggedInSuccess(user: User){
        hideProgressDialog()

        Log.i("Username", user.userName)
        Log.i("Email", user.email)

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
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