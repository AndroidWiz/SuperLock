package com.sk.superlock.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.ActivityChangePinBinding
import com.sk.superlock.fragment.SetPinFragment

class ChangePinActivity : BaseActivity() {

    private lateinit var binding: ActivityChangePinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarChangePinActivity)
        supportActionBar?.title = ""

        showPinChangeFragment(SetPinFragment())
    }

    // show login fragment view
    private fun showPinChangeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.changePinFragmentHost.id, fragment)
            .commit()
    }
}