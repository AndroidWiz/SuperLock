package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.sk.superlock.R
import com.sk.superlock.data.model.ResetResponse
import com.sk.superlock.databinding.ActivityForgotPasswordBinding
import com.sk.superlock.services.ApiClient
import com.sk.superlock.services.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var apiInterface: ApiInterface

    companion object{
        const val TAG = "ForgotPasswordActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarForgotPassActivity)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white)

        apiInterface = ApiClient.getClient(this@ForgotPasswordActivity).create(ApiInterface::class.java)

        binding.btnSend.setOnClickListener {
            resetPassword()
        }
    }


    // send email to reset password
    private fun resetPassword(){
        val email = binding.etEmail.text.toString().trim{ it <= ' '}
        if(email.isEmpty()){
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
        }else{
            showProgressDialog(resources.getString(R.string.please_wait))
            sendResetEmail(email)
        }
    }

    private fun sendResetEmail(email: String){
        apiInterface.resetUserPassword(email)
            .enqueue(object: Callback<ResetResponse>{
                override fun onResponse(
                    call: Call<ResetResponse>,
                    response: Response<ResetResponse>
                ) {
                    hideProgressDialog()
                    if(response.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity,
                            resources.getString(R.string.email_sent_to_retrieve_password), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
                        finish()

                    }else{
                        Toast.makeText(this@ForgotPasswordActivity,
                            resources.getString(R.string.email_sending_failed_to_retrieve_password), Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Something went wrong sending email")
                    }
                }

                override fun onFailure(call: Call<ResetResponse>, t: Throwable) {
                    hideProgressDialog()
                    Toast.makeText(this@ForgotPasswordActivity,
                        resources.getString(R.string.email_sending_failed_to_retrieve_password), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Email sending failed", t)
                }

            })
    }
}