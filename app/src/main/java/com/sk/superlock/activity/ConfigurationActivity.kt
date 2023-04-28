package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import com.sk.superlock.databinding.ActivityConfigurationBinding
import com.sk.superlock.fragment.LoginCameraFragment
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
        }
    }



    private fun showFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.settingsFragmentHost.id, LoginCameraFragment())
            .commit()
    }
}