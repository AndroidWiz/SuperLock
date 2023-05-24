package com.sk.superlock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityLockBinding
import com.sk.superlock.fragment.SetPinFragment
import com.sk.superlock.util.Constants

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isForConfiguration = false
        val args = Bundle().apply {
            putBoolean(Constants.ARGS_IS_CONFIGURATION, isForConfiguration)
        }

        setFragment(SetPinFragment().apply { arguments = args })
//        setFragment(CompareFaceFragment())
    }

    // check PIN
    private fun validatePIN(){

    }

    // set default fragment
    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_lock_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}