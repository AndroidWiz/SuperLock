package com.sk.superlock.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.ActivityChangePinBinding
import com.sk.superlock.fragment.SetPinFragment
import com.sk.superlock.util.Constants

class ChangePinActivity : BaseActivity() {

    private lateinit var binding: ActivityChangePinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarChangePinActivity)
        supportActionBar?.title = ""

        val isForConfiguration = true
        val args = Bundle().apply {
            putBoolean(Constants.ARGS_IS_CONFIGURATION, isForConfiguration)
        }

        showPinChangeFragment(SetPinFragment().apply { arguments = args })
    }

    // show login fragment view
    private fun showPinChangeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.changePinFragmentHost.id, fragment)
            .commit()
    }
}