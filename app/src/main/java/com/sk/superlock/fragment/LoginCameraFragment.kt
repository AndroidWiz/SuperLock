package com.sk.superlock.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.FragmentLoginCameraBinding


class LoginCameraFragment : Fragment() {

    private lateinit var binding: FragmentLoginCameraBinding

    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            if (::cameraDevice.isInitialized) {
                startCameraPreview()
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
        if (requiredPermissions.all { ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED }) {
            // Permission is granted
            startCameraPreview()
           Toast.makeText(requireContext(), "Permission Granted Already", Toast.LENGTH_SHORT).show()
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 1)
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        }

        val manager = requireContext().getSystemService(android.content.Context.CAMERA_SERVICE) as CameraManager
        val cameraId = manager.cameraIdList[0]

        manager.openCamera(cameraId, stateCallback, null)

        // capture button
        binding.captureButton.setOnClickListener {
            takePicture()
        }

        // cancel button
        binding.cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        return view
    }

    // open camera and start preview
    private fun startCameraPreview() {
        val previewSurfaceTexture = TextureView(requireContext()).surfaceTexture
        previewSurfaceTexture?.setDefaultBufferSize(
            binding.cameraPreviewContainer.width,
            binding.cameraPreviewContainer.height
        )
        val previewSurface = Surface(previewSurfaceTexture)

        val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(previewSurface)

        cameraDevice.createCaptureSession(
            listOf(previewSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    cameraCaptureSession.setRepeatingRequest(
                        previewRequestBuilder.build(),
                        null,
                        null
                    )
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("LoginCameraFragment", "Failed to create capture session")
                }
            },
            null
        )
    }


    // take a picture
    private fun takePicture() {
        val imageReader = ImageReader.newInstance(
            binding.cameraPreviewContainer.width,
            binding.cameraPreviewContainer.height,
            ImageFormat.JPEG,
            1
        )
        val outputSurfaces = mutableListOf<Surface>()
        outputSurfaces.add(imageReader.surface)

        val captureRequestBuilder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReader.surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

        val captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                val image = imageReader.acquireLatestImage()
                // process the image
                image?.close()
                startCameraPreview()
            }
        }

        cameraCaptureSession.stopRepeating()
        cameraCaptureSession.capture(captureRequestBuilder.build(), captureCallback, null)
    }

    // request permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permission granted
            } else {
                // Permission denied
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraDevice.isInitialized) {
            cameraDevice.close()
        }
        if (::cameraCaptureSession.isInitialized) {
            cameraCaptureSession.close()
        }
    }



}

/*private fun takePicture() {
        val imageReader = ImageReader.newInstance(binding.cameraPreviewContainer.width, binding.cameraPreviewContainer.height, ImageFormat.JPEG, 1)
        val outputSurfaces = mutableListOf<Surface>()
        outputSurfaces.add(imageReader.surface)

        val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReader.surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

        val captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                super.onCaptureCompleted(session, request, result)
                val image = imageReader.acquireLatestImage()
                // process the image
                image?.close()
                startCameraPreview()
            }
        }

        cameraCaptureSession.stopRepeating()
        cameraCaptureSession.capture(captureRequestBuilder.build(), captureCallback, null)
    }*/

/* private fun startCameraPreview() {
     val previewSurface = binding.cameraPreviewContainer.surfaceTexture
     camera.startPreview(previewSurface)

     val previewRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
     previewRequestBuilder.addTarget(previewSurface)

     camera.createCaptureSession(listOf(previewSurface), object : CameraCaptureSession.StateCallback() {
         override fun onConfigured(session: CameraCaptureSession) {
             cameraCaptureSession = session
             cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null)
         }

         override fun onConfigureFailed(session: CameraCaptureSession) {
             Log.e("LoginCameraFragment", "Failed to create capture session")
         }
     }, null)
 }*/


/*
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
}*/

/*   override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
        cameraCaptureSession.close()
    }*/

/*    override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startCameraPreview()
        }*/