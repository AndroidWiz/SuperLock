package com.sk.superlock.activity

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarForgotPassActivity)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white)
    }


    // send email to reset password
    private fun resetPassword(){
        val email = binding.etEmail.text.toString().trim{ it <= ' '}
        if(email.isEmpty()){
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
        }else{
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity, resources.getString(R.string.email_sent_to_retrieve_password), Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}