package com.sk.superlock.activity

import android.os.Bundle
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityConfigurationBinding

class ConfigurationActivity : BaseActivity() {

    private lateinit var binding: ActivityConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
    }


    // set up toolbar
    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbarConfigurationActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button)
        }
        binding.toolbarConfigurationActivity.setNavigationOnClickListener { onBackPressed() }
    }
}