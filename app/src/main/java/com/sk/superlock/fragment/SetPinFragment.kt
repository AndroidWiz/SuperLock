package com.sk.superlock.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.activity.MainActivity
import com.sk.superlock.data.model.Intruder
import com.sk.superlock.databinding.FragmentSetPinBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.PrefManager
import java.io.File
import java.util.concurrent.Executors

class SetPinFragment : Fragment() {

    private lateinit var binding: FragmentSetPinBinding
    private var isForConfiguration: Boolean = false
    val TAG = "SetPinFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetPinBinding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let{
            isForConfiguration = it.getBoolean(Constants.ARGS_IS_CONFIGURATION, false)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isForConfiguration) {
            binding.tvSetOrEnterPin.text = resources.getString(R.string.set_pin)
            binding.tvNextOrConfirmPin.text = resources.getString(R.string.confirm_pin)
        } else {
            binding.tvSetOrEnterPin.text = resources.getString(R.string.enter_pin)
            binding.tvNextOrConfirmPin.text = resources.getString(R.string.enter_pin)
        }

        binding.btn0.setOnClickListener { onNumberButtonClick(binding.btn0.text.toString()) }
        binding.btn1.setOnClickListener { onNumberButtonClick(binding.btn1.text.toString()) }
        binding.btn2.setOnClickListener { onNumberButtonClick(binding.btn2.text.toString()) }
        binding.btn3.setOnClickListener { onNumberButtonClick(binding.btn3.text.toString()) }
        binding.btn4.setOnClickListener { onNumberButtonClick(binding.btn4.text.toString()) }
        binding.btn5.setOnClickListener { onNumberButtonClick(binding.btn5.text.toString()) }
        binding.btn6.setOnClickListener { onNumberButtonClick(binding.btn6.text.toString()) }
        binding.btn7.setOnClickListener { onNumberButtonClick(binding.btn7.text.toString()) }
        binding.btn8.setOnClickListener { onNumberButtonClick(binding.btn8.text.toString()) }
        binding.btn9.setOnClickListener { onNumberButtonClick(binding.btn9.text.toString()) }

        binding.btnBackspace.setOnClickListener {
            val currentText = binding.tvPin.text.toString()
            if (currentText.isNotEmpty()) {
                binding.tvPin.text = currentText.substring(0, currentText.length - 1)
            }
        }

        binding.tvNextOrConfirmPin.setOnClickListener {
            val pin = binding.tvPin.text.toString()
            if(pin.isNotEmpty() && pin.length == 4){
                if(isForConfiguration){
                    PrefManager(requireContext()).setPin(pin)
                    Toast.makeText(requireContext(), resources.getString(R.string.pin_set_successful), Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }else{
                    if(PrefManager(requireContext()).verifyPin(pin)){
                        Toast.makeText(requireContext(), resources.getString(R.string.pin_verified_successful), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }else{
                        Toast.makeText(requireContext(), resources.getString(R.string.invalid_pin), Toast.LENGTH_LONG).show()
                        binding.tvPin.text = ""

                        takeIntruderPhoto()
                    }
                }
            }else{
                Toast.makeText(requireContext(), resources.getString(R.string.invalid_pin_length), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun onNumberButtonClick(number: String) {
        val currentText = binding.tvPin.text.toString()
        if (currentText.length < 4) {
            val newText = currentText + number
            binding.tvPin.text = newText
        }
    }

    // click intruder image
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private fun takeIntruderPhoto(){
        val imageFile = File(requireContext().externalMediaDirs.first(), "intruder_img_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture)
                imageCapture.takePicture(outputOptions, cameraExecutor, object: ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val intruder = Intruder(imageFile.absolutePath, System.currentTimeMillis())
                        PrefManager(requireContext()).saveIntruder(intruder)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Intruder image capture failed: ${exception.message}", exception)
                    }
                })
            } catch (exc: Exception) {
                Log.e(TAG, "Error capturing intruder image: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


}