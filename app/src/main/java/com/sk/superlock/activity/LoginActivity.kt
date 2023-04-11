package com.sk.superlock.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.storage.ktx.storage
import com.sk.superlock.R
import com.sk.superlock.data.Firestore
import com.sk.superlock.data.model.User
import com.sk.superlock.databinding.ActivityLoginBinding
import com.sk.superlock.fragment.LoginCameraFragment
import com.sk.superlock.fragment.LoginUsernameFragment
import com.sk.superlock.util.Constants
import com.sk.superlock.util.LoginMode
import kotlinx.android.synthetic.main.fragment_login_username.*
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginMode: LoginMode? = null
    private val userImagesList = CopyOnWriteArrayList<File>()

    companion object {
        val TAG = "LoginActivity"
//        val userImagesList = listOf<File>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

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
                    showProgressDialog("Please wait")

                    val email: String = et_email.text.toString().trim()
                    val password: String = et_password.text.toString().trim()

                    // check if text views are empty
                    if (TextUtils.isEmpty(email)) {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    if (TextUtils.isEmpty(password)) {
                        showErrorSnackBar(
                            resources.getString(R.string.err_msg_enter_password),
                            true
                        )
                    }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Firestore().getUserDetails(this)
                            } else {
                                hideProgressDialog()
                                showErrorSnackBar(task.exception!!.message.toString(), true)
                            }
                        }
                }
                LoginMode.CAMERA -> {
                    /*Thread {

                    }.start()*/
                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser

                    if (user != null) {
                        user.getIdToken(true)
                            .addOnCompleteListener { task: Task<GetTokenResult> ->
                                if (task.isSuccessful) {
                                    val token = task.result.token
                                    // use the token
                                } else {
                                    // handle the error
                                }
                            }
                    } else {
                        val capturedImageUri = LoginCameraFragment.savedUri
                        capturedImageUri?.let { it ->
                            compareCapturedImageWithUserImages(
                                it
                            )
                        }
                    }

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

    private fun downloadUserImages(): List<File> {
        val storageRef = Firebase.storage.reference

        // construct a reference to the "user-images/" folder
        val userImagesRef = storageRef.child(Constants.USER_IMAGES_PATH)

        // list all the items (images) in the "user-images/" folder
        userImagesRef.listAll().addOnSuccessListener { listResult ->
            // loop through each item and download it to the cache directory
            listResult.items.forEach { item ->
                // construct a reference to the local file to save the image to
                val localFile = File.createTempFile(item.name, "jpg", cacheDir)

                // download the image to the local file
                item.getFile(localFile).addOnSuccessListener {
                    Log.d(TAG, "Image downloaded to ${localFile.absolutePath}")
                    // add the downloaded file to the list of downloaded images
                    userImagesList.add(localFile)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Image download failed: ${exception.message}", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "List items failed: ${exception.message}", exception)
        }
        // return an empty list for now, as the images are not yet downloaded
        return emptyList()
    }


    //get all images from cloud
    private fun compareCapturedImageWithUserImages(capturedImageUri: Uri) {
        val faceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .build()

        val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(faceDetectorOptions)

        // Download all user images from Firestore storage
        downloadUserImages()

        // Create a bitmap from the captured image
        val capturedImageBitmap = MediaStore.Images.Media.getBitmap(
            contentResolver, capturedImageUri
        )

        // Detect face in the captured image
        val capturedImageVisionImage = FirebaseVisionImage.fromBitmap(capturedImageBitmap)
        faceDetector.detectInImage(capturedImageVisionImage)
            .addOnSuccessListener { capturedImageFaces ->
                if (capturedImageFaces.isNotEmpty()) {
                    // Iterate through all downloaded user images and compare them with the captured image
                    val userImages = downloadUserImages()
                    for (userImage in userImages) {
                        val userImageBitmap = BitmapFactory.decodeFile(userImage.absolutePath)
                        val userImageVisionImage = FirebaseVisionImage.fromBitmap(userImageBitmap)
                        faceDetector.detectInImage(userImageVisionImage)
                            .addOnSuccessListener { userImageFaces ->
                                if (userImageFaces.isNotEmpty()) {
                                    // Compare the detected faces in the captured image and the user image
                                    val capturedImageFace = capturedImageFaces[0]
                                    val userImageFace = userImageFaces[0]
                                    val faceMatched = capturedImageFace.headEulerAngleY ==
                                            userImageFace.headEulerAngleY &&
                                            capturedImageFace.headEulerAngleZ ==
                                            userImageFace.headEulerAngleZ &&
                                            capturedImageFace.boundingBox ==
                                            userImageFace.boundingBox
                                    if (faceMatched) {
                                        // Faces matched, allow user to login
                                        Toast.makeText(
                                            this,
                                            "Faces matched, user can login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d(TAG, "Faces matched, user can login")
                                        // Do something here
                                        Toast.makeText(
                                            this,
                                            "Faces not matched, user cannot login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d(TAG, "Faces not matched, user cannot login")
                                    }
                                }
                            }
                    }
                }
            }
    }


    // login success
    fun userLoggedInSuccess(user: User) {
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
//            is CameraLoginFragment -> LoginMode.CAMERA
            is LoginCameraFragment -> LoginMode.CAMERA
            else -> null
        }
    }


}