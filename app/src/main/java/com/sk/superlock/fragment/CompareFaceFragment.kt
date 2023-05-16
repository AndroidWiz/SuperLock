package com.sk.superlock.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.data.model.FaceResponse
import com.sk.superlock.databinding.FragmentCompareFaceBinding
import com.sk.superlock.services.ApiClient
import com.sk.superlock.services.ApiInterface
import com.sk.superlock.util.Constants
import com.sk.superlock.util.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class CompareFaceFragment : Fragment() {

    private lateinit var binding: FragmentCompareFaceBinding
    private lateinit var apiInterface: ApiInterface
    companion object{
        const val TAG: String = "CompareFaceFragment"
        var accessToken: String = ""
        var mCapturedImage: Uri? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCompareFaceBinding.inflate(inflater, container, false)
        val view = binding.root

        apiInterface = ApiClient.getClient(requireContext()).create(ApiInterface::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun uploadImage(accessToken: String, imageBytes: ByteArray){
        val mediaType = "image/*".toMediaType()
        val requestBody = imageBytes.toRequestBody(mediaType)
        val filePart = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

        apiInterface.compareFace(accessToken, filePart)
            .enqueue(object: Callback<FaceResponse> {
                override fun onResponse(
                    call: Call<FaceResponse>,
                    response: Response<FaceResponse>
                ) {
                    if(response.isSuccessful){
                        val faceResponse = response.body()
                    }else{

                    }
                }

                override fun onFailure(call: Call<FaceResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Something went wrong", t)
                }

            })
    }

    // image chooser
    @Suppress("DEPRECATION")
    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(cameraIntent, Constants.OPEN_CAMERA_PERMISSION_CODE)
        } else {
            Toast.makeText(requireContext(), resources.getString(R.string.camera_unavailable), Toast.LENGTH_SHORT).show()
        }
    }

    // request read storage permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // if permission granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // get image uri to show in the imageview
    @Suppress("DEPRECATION")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Compare_Image",
            null
        )
        return Uri.parse(path)
    }

    // get the real path from a content uri
    private fun getRealPathFromUri(uri: Uri): String {
        val filePath: String
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
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

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.OPEN_CAMERA_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                mCapturedImage = getImageUri(requireContext(), imageBitmap)
//                binding.ivUploadUserImage.setImageURI(mCapturedImage)
                try {
                    GlideLoader(requireContext()).loadUserPicture(mCapturedImage!!, iv_upload_user_image)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), resources.getString(R.string.img_selection_failed), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Image Request Cancelled", "Image selection cancelled by user.")
        }
    }
}