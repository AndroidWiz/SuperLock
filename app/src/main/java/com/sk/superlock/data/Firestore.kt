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
    fun registerUser(activity: RegisterActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{ e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user", e)
            }

    }

    // upload image to cloud storage
    fun uploadImageToStorage(activity: RegisterActivity, imageFileUrl: Uri?, imageType: String){
        val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child(imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileUrl))

        storageRef.putFile(imageFileUrl!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable image url", uri.toString())
                        activity.imageUploadSuccess(uri.toString())
                    }
            }
            .addOnFailureListener{e ->
                activity.hideProgressDialog()
                Log.d(activity.javaClass.simpleName, e.message, e)
            }
    }


    // update profile with image
    fun updateUserProfileData(activity: RegisterActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                activity.userProfileUpdated()
            }
            .addOnFailureListener {e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating image", e)
            }
    }


    // get current user id
    fun getCurrentUserId(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""
        if (currentUser != null){
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
                //Key: logged_in_username
                //value: firstname and lastName
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

    /*fun registerUser(activity: RegisterActivity, userInfo: User, imageUri: Uri){
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("${Constants.USERS}/${userInfo.id}.jpg")
        val uploadTask = storageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if(!task.isSuccessful){
                task.exception?.let{
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful){
                val downloadUri = task.result.toString()

                // add image download URL to user info
                userInfo.profilePicture = downloadUri

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
            }else{
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error uploading profile image", task.exception)
            }
        }
    }*/
}