package com.sk.superlock.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.storage.ktx.storage
import com.sk.superlock.databinding.ActivityConfigurationBinding
import com.sk.superlock.fragment.LoginCameraFragment
import com.sk.superlock.util.Constants
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class ConfigurationActivity : BaseActivity() {

    private lateinit var binding: ActivityConfigurationBinding

    private val userImagesList = CopyOnWriteArrayList<File>()
    val TAG = "ConfigurationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarConfigurationActivity)

        // button change pin
        binding.rlChangePin.setOnClickListener {
            startActivity(Intent(this@ConfigurationActivity, ChangePinActivity::class.java))
        }

        showFragment()
        binding.btnCheck.setOnClickListener {
            compareCapturedImageWithUserImages(LoginCameraFragment.savedUri!!)
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
        return userImagesList
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

        // Create a bitmap from the captured image
        val capturedImageBitmap = MediaStore.Images.Media.getBitmap(
            contentResolver, capturedImageUri
        )

        // Detect face in the captured image
        val capturedImageVisionImage = FirebaseVisionImage.fromBitmap(capturedImageBitmap)
        faceDetector.detectInImage(capturedImageVisionImage)
            .addOnSuccessListener { capturedImageFaces ->
                if (capturedImageFaces.isNotEmpty()) {
                    var foundMatch = false
                    // Iterate through all downloaded user images and compare them with the captured image
                    downloadUserImages().forEach { userImage ->
                        val userImageBitmap = BitmapFactory.decodeFile(userImage.absolutePath)
                        val userImageVisionImage = FirebaseVisionImage.fromBitmap(userImageBitmap)
                        faceDetector.detectInImage(userImageVisionImage)
                            .addOnSuccessListener { userImageFaces ->
                                if (userImageFaces.isNotEmpty() && !foundMatch) {
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
                                        foundMatch = true
                                    }
                                }
                            }
                    }
                    if (!foundMatch) {
                        // No match found, user cannot login
                        Toast.makeText(
                            this,
                            "No match found, user cannot login",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "No match found, user cannot login")
                    }
                }
            }
    }



    private fun showFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.settingsFragmentHost.id, LoginCameraFragment())
            .commit()
    }
}