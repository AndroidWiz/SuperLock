package com.sk.superlock.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_camera_login.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias CornersListener = () -> Unit

class CameraLoginFragment : Fragment() {

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var safeContext: Context

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.sk.superlock.R.layout.fragment_camera_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Setup the listener for take photo button
        capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

//        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraExecutor = Executors.newCachedThreadPool()
    }

    private fun startCamera() {
        OpenCVLoader.initDebug()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder().build()

            imageAnalyzer = ImageAnalysis.Builder().build().apply {
                setAnalyzer(Executors.newSingleThreadExecutor(), CornerAnalyzer {
                    val bitmap = cam_preview?.bitmap
                    if (bitmap != null) {
                        Handler(Looper.getMainLooper()).post {
                            val img = Mat()
                            Utils.bitmapToMat(bitmap, img)
                            bitmap.recycle()
                            // Do image analysis here if you need the Mat object
                        }
                    }
                })
            }


            // Select front camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer, preview, imageCapture)
                preview?.setSurfaceProvider(cam_preview.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(safeContext))

    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(safeContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    override fun onPause() {
        super.onPause()
        isOffline = true
    }

    override fun onResume() {
        super.onResume()
        isOffline = false
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(safeContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    safeContext,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
//                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getOutputDirectory(): File {
//        val mediaDir = requireActivity().externalMediaDirs?.firstOrNull()?.let {
        val mediaDir = requireContext().externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(com.sk.superlock.R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireContext().filesDir!!
//        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir!!
    }


    companion object {
        val TAG = "CameraLoginFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        internal const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var isOffline = false // prevent app crash when goes offline
    }

 private class CornerAnalyzer(private val listener: CornersListener) : ImageAnalysis.Analyzer {

     private fun ByteBuffer.toByteArray(): ByteArray {
         rewind()    // Rewind the buffer to zero
         val data = ByteArray(remaining())
         get(data)   // Copy the buffer into a byte array
         return data // Return the byte array
     }

     @SuppressLint("UnsafeExperimentalUsageError")
     override fun analyze(imageProxy: ImageProxy) {
         // Get the image buffer
         val buffer = imageProxy.planes[0].buffer
         val data = buffer.toByteArray()

         // Get the image dimensions
         val imageWidth = imageProxy.width
         val imageHeight = imageProxy.height

         // Close the image proxy
         if(!isOffline){
             listener
         }
         imageProxy.close()
     }

 }

}