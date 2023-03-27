package com.sk.superlock.activity

import android.os.Bundle
import com.sk.superlock.databinding.ActivityConfigurationBinding

class ConfigurationActivity : BaseActivity() {

    private lateinit var binding: ActivityConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarConfigurationActivity)
    }


}