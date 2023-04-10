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
import com.sk.superlock.data.model.UserProfileImage
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.CameraLoginFragment
import com.sk.superlock.fragment.LoginUsernameFragment
import com.sk.superlock.util.Constants
import kotlinx.android.synthetic.main.fragment_login_username.*
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File

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
//                    showLoginFragment(LoginCameraFragment())
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
                LoginMode.CAMERA -> {
                   /* Thread{
                        val user = loginWithProfileImage(CameraLoginFragment.savedUri.toString())

                        runOnUiThread{
                            // check if the user exists and the image matches
                            if (user == null) {
                                Toast.makeText(this, "Cannot authenticate user", Toast.LENGTH_SHORT).show()
                            } else {
                                // save the user ID to shared preferences
                                val prefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
                                prefs.edit().putLong(Constants.USER_ID, user.id!!).apply()

                                // start the main activity
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }

                    }.start()*/
                    Thread {
                        val user = loginWithProfileImage(CameraLoginFragment.savedUri.toString())

                        runOnUiThread {
                            // check if the user exists and the image matches
                            if (user == null) {
                                Toast.makeText(this, "Cannot authenticate user", Toast.LENGTH_SHORT).show()
                            } else {
                                // save the user ID to shared preferences
                                val prefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
                                prefs.edit().putLong(Constants.USER_ID, user.id).apply()

                                // login the user
                                val loggedInUser = login(user.username, user.password)

                                // check if login was successful
                                if (loggedInUser == null) {
                                    Toast.makeText(this, "Cannot login user", Toast.LENGTH_SHORT).show()
                                } else {
                                    // save the logged-in user ID to shared preferences
                                    prefs.edit().putLong(Constants.LOGGED_IN_USER_ID, loggedInUser.id!!).apply()

                                    // start the main activity
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                        }
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

    // login using camera
//    private fun loginWithProfileImage(profileImagePath: String): User? {
    private fun loginWithProfileImage(profileImagePath: String): UserProfileImage? {
        // Load the captured image
        val profileImage = Imgcodecs.imread(profileImagePath)

        // Load the registered images
        val registeredImages = getAllRegisteredImages()

        // Loop through the registered images and compare each one to the captured image
        for (registeredImage in registeredImages) {
            // Load the registered image
//            val registeredImageMat = Imgcodecs.imread(registeredImage.profilePicture)
            val registeredImageMat = Imgcodecs.imread(registeredImage.filePath)

            // Resize the captured image and registered image to the same size
            val resizedProfileImage = Mat()
            Imgproc.resize(profileImage, resizedProfileImage, registeredImageMat.size())

            // Compute the difference between the two images
            val diff = Mat()
            Core.absdiff(resizedProfileImage, registeredImageMat, diff)

            // Compute the sum of the differences
            val sum = Core.sumElems(diff)

            // Compute the average difference
            val avgDiff = sum.`val`[0] / (resizedProfileImage.rows() * resizedProfileImage.cols())

            // If the average difference is below a certain threshold, the images match
            if (avgDiff < 50) {
                return registeredImage
            }
        }

        // If no matching user is found, return null
        return null
    }

    /*   fun loginWithProfileImage(profileImagePath: String): User? {
           val userWithProfileImage = userDao.getUserByProfilePicture(profileImagePath)
           if (userWithProfileImage != null) {
               return userWithProfileImage
           } else {
               return null
           }
       }*/


    // get all registered user images
    private fun getAllRegisteredImages(): List<UserProfileImage> {
        // Initialize OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val images = mutableListOf<UserProfileImage>()

        // Replace this path with the directory containing your registered user images
//        val dirPath = "/path/to/registered/images"
        val dirPath = "/storage/emulated/0/DCIM/Camera/"

        // Loop through all image files in the directory
        val dir = File(dirPath)
        val files = dir.listFiles()
        for ((id, file) in files.withIndex()) {
            if (file.isFile) {
                val filePath = file.absolutePath
                val image = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE)

                // Resize the image to a standard size
                Imgproc.resize(image, image, Size(300.0, 300.0))

                images.add(UserProfileImage(id, filePath, image))
            }
        }

        return images
    }
/*    fun getAllRegisteredImages(): List<Mat> {
        // Initialize OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val images = mutableListOf<Mat>()

        // Replace this path with the directory containing your registered user images
        val dirPath = "/path/to/registered/images"

        // Loop through all image files in the directory
        val dir = File(dirPath)
        val files = dir.listFiles()
        for (file in files) {
            if (file.isFile) {
                val filePath = file.absolutePath
                val image = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE)

                // Resize the image to a standard size
                Imgproc.resize(image, image, Size(300.0, 300.0))

                images.add(image)
            }
        }

        return images
    }*/


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