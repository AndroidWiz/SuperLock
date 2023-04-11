package com.sk.superlock.fragment

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.FragmentLoginCameraBinding
import com.sk.superlock.util.GlideLoader
import kotlinx.android.synthetic.main.fragment_login_camera.*
import java.io.File

class LoginCameraFragment : Fragment() {

    private lateinit var binding: FragmentLoginCameraBinding

    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture


    companion object {
        const val TAG = "LoginCameraFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var savedUri: Uri? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        previewView = binding.camPreview

        val cameraProviderFeature = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFeature.addListener({
            val cameraProvider = cameraProviderFeature.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))


        // capture button
        binding.captureButton.setOnClickListener {
            capturePhoto()
        }


        return view

    }

    // capture photo
    private fun capturePhoto(){
        val file = File(requireContext().externalMediaDirs.first(),
            "${System.currentTimeMillis()}.jpg")
//        saved image location
//        /storage/emulated/0/Android/media/com.sk.superlock/1681210796902.jpg

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // image saved successfully, do something with it
                    savedUri = Uri.fromFile(file)

                    // flip the captured image horizontally to remove mirror effect
                    val matrix = Matrix().apply { postScale(-1f, 1f, savedUri?.let { image_preview.width.toFloat() / 2 } ?: 0f, 0f) }
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, savedUri)
                    val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                    GlideLoader(requireContext()).capturedPicture(flippedBitmap, image_preview)
                    previewView.visibility = View.GONE
                    image_preview.visibility = View.VISIBLE
                    Log.d(TAG, "Image saved to ${file.absolutePath}")
                    Log.d(TAG, savedUri.toString())
                }

                override fun onError(exception: ImageCaptureException) {
                    // image capture failed, handle the error
                    Log.e(TAG, "Image capture failed: ${exception.message}", exception)
                }
            })
    }


}

