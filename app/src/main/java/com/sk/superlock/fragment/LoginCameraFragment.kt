package com.sk.superlock.fragment

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sk.superlock.R
import com.sk.superlock.databinding.FragmentLoginCameraBinding

class LoginCameraFragment : Fragment() {

    private lateinit var binding: FragmentLoginCameraBinding
    /* private lateinit var camera: Camera
     private lateinit var faceDetector: FaceDetector

     private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
     private lateinit var imageCapture: ImageCapture
     private lateinit var cameraExecutor: ExecutorService*/


    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button

    private val imageCapture = ImageCapture.Builder().build()

    companion object {
        const val TAG = "LoginCameraFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        previewView = view.findViewById(R.id.cam_preview)
        captureButton = view.findViewById(R.id.capture_button)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))

        captureButton.setOnClickListener {
            captureImage()
        }

        return view

    }

    // show image preview
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        val imageAnalysis = ImageAnalysis.Builder().build()

        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error binding camera", e)
        }
    }

    // capture image
    private fun captureImage() {
       /* val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "${System.currentTimeMillis()}.jpg"
        ).build()*/

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        }
        Log.d(TAG, "savedImage: $contentValues")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()


        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: return
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        savedUri
                    )
                    // The bitmap is saved to the file and can be used for face detection
                    detectFaces(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error capturing image", exception)
                }
            }
        )
    }


    // detect face in image
    private fun detectFaces(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(options)
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Faces detected successfully
                if (faces.isNotEmpty()) {
                    // One or more faces detected, perform authentication
                    authenticateUser()
                } else {
                    // No faces detected
                    Toast.makeText(requireContext(), "No faces detected, please try again", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                // Error occurred during face detection
                Toast.makeText(requireContext(), "Error detecting faces: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    // authenticate user to login
    private fun authenticateUser() {
        // Perform authentication logic here
        Toast.makeText(requireContext(), "User authenticated successfully", Toast.LENGTH_SHORT).show()
    }



}


/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        // Check camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.captureButton.setOnClickListener {
            takePhoto()
        }

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)


        return view

override fun onDestroyView() {
    super.onDestroyView()
    cameraExecutor.shutdown()
}

private fun startCamera() {
    cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

    cameraProviderFuture.addListener({
        // Bind the preview, image capture and image analysis use cases to the lifecycle
        // of this fragment
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(cam_preview.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        try {
            // Unbind any bound use cases before rebinding
            cameraProvider.unbindAll()

            // Bind preview, image capture and image analysis use cases
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        } catch (e: Exception) {
            Log.e(LoginCameraFragment.TAG, "Use case binding failed", e)
        }

    }, ContextCompat.getMainExecutor(requireContext()))
}


private fun takePhoto() {
    // Create a filename with current timestamp
    val filename = "${System.currentTimeMillis()}.jpg"

    // Create output file location
    val outputDirectory = requireContext().externalCacheDir ?: return
    val photoFile = File(outputDirectory, filename)

    // Create output options object which contains the file and its metadata
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // Set up image capture use case and attach listener
    val imageCapture = imageCapture ?: return
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Image saved successfully, process the image for face detection
                val savedUri = Uri.fromFile(photoFile)
                detectFaces(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                // Error occurred while saving the image, show error message
                Toast.makeText(
                    requireContext(),
                    "Error taking photo: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
}

// detect faces
fun detectFaces(bitmap: Bitmap): List<Face> {
    val image = InputImage.fromBitmap(bitmap, 0)
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.15f)
        .enableTracking()
        .build()

    val detector = FaceDetection.getClient(options)
    val task = detector.process(image)

    return try {
        val result = Tasks.await(task, 10, TimeUnit.SECONDS)
        val faces = mutableListOf<Face>()
        for (face in result) {
            faces.add(face)
        }
        faces
    } catch (e: Exception) {
        Log.e(LoginCameraFragment.TAG, "Error detecting faces: ${e.message}")
        emptyList()
    } finally {
        detector.close()
    }
}
    }*/

/*  private fun startCamera() {
      val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
      cameraProviderFuture.addListener({
          val cameraProvider = cameraProviderFuture.get()

          val preview = Preview.Builder()
              .build()
              .also {
                  it.setSurfaceProvider(cam_preview.surfaceProvider)
              }

          imageCapture = ImageCapture.Builder()
              .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
              .build()

          val imageAnalyzer = ImageAnalysis.Builder()
              .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
              .build()
              .also {
                  it.setAnalyzer(cameraExecutor, FaceAnalyzer { faces ->
                      // Perform face matching logic here
                  })
              }

          val cameraSelector = CameraSelector.Builder()
              .requireLensFacing(CameraSelector.LENS_FACING_BACK)
              .build()

          try {
              cameraProvider.unbindAll()

              camera = cameraProvider.bindToLifecycle(
                  this, cameraSelector, preview, imageCapture, imageAnalyzer)

          } catch (exc: Exception) {
              Log.e(TAG, "Error binding camera", exc)
          }
      }, ContextCompat.getMainExecutor(requireContext()))
  }

  private class FaceAnalyzer(private val callback: (List<Face>) -> Unit) : ImageAnalysis.Analyzer {

      private val detector = FaceDetection.getClient()

      override fun analyze(imageProxy: ImageProxy) {
          val mediaImage = imageProxy.image ?: return

          val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

          detector.process(image)
              .addOnSuccessListener { faces ->
                  callback(faces)
              }
              .addOnFailureListener { exc ->
                  Log.e(TAG, "Error detecting faces", exc)
              }
              .addOnCompleteListener {
                  imageProxy.close()
              }
      }
  }
*/
