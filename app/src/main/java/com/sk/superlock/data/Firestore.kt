package com.sk.superlock.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sk.superlock.activity.LoginActivity
import com.sk.superlock.activity.RegisterActivity
import com.sk.superlock.data.model.User
import com.sk.superlock.util.Constants

class Firestore {

    private val mFireStore = FirebaseFirestore.getInstance()

    // register user
    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user", e)
            }

    }

    // upload image to cloud storage
    fun uploadImageToStorage(activity: RegisterActivity, imageFileUrl: Uri?, imageType: String) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_IMAGES_PATH + imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUrl
            )
        )

        storageRef.putFile(imageFileUrl!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable image url", uri.toString())
                        activity.imageUploadSuccess(uri.toString())
                    }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.d(activity.javaClass.simpleName, e.message, e)
            }
    }


    // update profile with image
    fun updateUserProfileData(activity: RegisterActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                activity.userProfileUpdated()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating image", e)
            }
    }


    // get current user id
    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }


    // get user details
    fun getUserDetails(activity: Activity) {
        //passing the collection name from which data is to be retrieved
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.APP_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.userName}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        //calling a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)
                    }
//                    is SettingsActivity -> {
//                        activity.userDetailsSuccess(user)
//                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
//                    is SettingsActivity -> {
//                        activity.hideProgressDialog()
//                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting user details.", e)
            }

    }


    // download all images from cloud firestore
//    fun getAllImagesFromCloudStorage(){
//        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(Constants.USER_IMAGES_PATH)
//    }
//    fun downloadUserImages(activity: LoginActivity): List<File> {
//        val storageRef = Firebase.storage.reference
//
//        // construct a reference to the "user-images/" folder
//        val userImagesRef = storageRef.child(Constants.USER_IMAGES_PATH)
//
//        // list all the items (images) in the "user-images/" folder
//        userImagesRef.listAll().addOnSuccessListener { listResult ->
//            // loop through each item and download it to the cache directory
//            listResult.items.forEach { item ->
//                // construct a reference to the local file to save the image to
//                val localFile = File.createTempFile(item.name, "jpg", activity.cacheDir)
//
//                // download the image to the local file
//                item.getFile(localFile).addOnSuccessListener {
//                    Log.d(activity.javaClass.simpleName, "Image downloaded to ${localFile.absolutePath}")
//                    // add the downloaded file to the list of downloaded images
//                    userImages.add(localFile)
//                }.addOnFailureListener { exception ->
//                    Log.e(activity.javaClass.simpleName, "Image download failed: ${exception.message}", exception)
//                }
//            }
//        }.addOnFailureListener { exception ->
//            Log.e(activity.javaClass.simpleName, "List items failed: ${exception.message}", exception)
//        }
//        return userImages
//    }

}