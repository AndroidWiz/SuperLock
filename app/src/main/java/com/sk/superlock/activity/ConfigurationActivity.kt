package com.sk.superlock.activity

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.sk.superlock.databinding.ActivityConfigurationBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.PrefManager
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

        // get the state of the switch that was saved earlier
        binding.switchFingerprint.isChecked =
            PrefManager(this).getBool(Constants.FINGERPRINT_SWITCH_STATE)
        binding.tvPasswordType.text = PrefManager(this).getString(Constants.PASSWORD_TYPE)

        // switch changes and updates textview accordingly
        binding.switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            val textViewText = if (isChecked) "Fingerprint" else "PIN"
            binding.tvPasswordType.text = textViewText
            PrefManager(this).saveBool(Constants.FINGERPRINT_SWITCH_STATE, isChecked)
            PrefManager(this).saveString(Constants.PASSWORD_TYPE, textViewText)

            if(isChecked){
                when{
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->{
                        checkBiometricSupport()
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->{
                        checkFingerprintSupport()
                    }
                    else ->{
                        Toast.makeText(this, "Fingerprint not supported on your device", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

//        showFragment()
//        binding.btnCheck.setOnClickListener {
//        }
    }

    // check fingerprint support for device
    private fun checkFingerprintSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fingerPrintManager = FingerprintManagerCompat.from(this)
            if (!fingerPrintManager.isHardwareDetected) {
                Toast.makeText(this, "Fingerprint not supported on your device.", Toast.LENGTH_LONG).show()
            } else if (!fingerPrintManager.hasEnrolledFingerprints()) {
                Toast.makeText(this, "No fingerprint enrolled.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Fingerprint not supported.", Toast.LENGTH_LONG).show()
        }
    }

    // check biometric support
    private fun checkBiometricSupport() {
//        if (BuildCompat.isAtLeastQ()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val biometricManager = getSystemService(BIOMETRIC_SERVICE) as BiometricManager
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    Toast.makeText(this, "Biometric Supported", Toast.LENGTH_SHORT).show()
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(this, "Biometric hardware not detected.", Toast.LENGTH_LONG).show()
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Toast.makeText(this, "Biometric hardware unavailable.", Toast.LENGTH_LONG).show()
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Toast.makeText(this, "Biometric not enrolled.", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(this, "Biometric not supported.", Toast.LENGTH_SHORT).show()
        }
    }


//    private fun showFragment() {
//        supportFragmentManager.beginTransaction()
//            .replace(binding.settingsFragmentHost.id, LoginCameraFragment())
//            .commit()
//    }
}