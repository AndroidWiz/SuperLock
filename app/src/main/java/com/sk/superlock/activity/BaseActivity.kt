package com.sk.superlock.activity

import android.app.Dialog
import android.content.res.Configuration
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sk.superlock.R
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog
    private lateinit var mNoInternetConnectionDialog: Dialog


    // set language according to local settings of device
    fun setLocale(){
//        val locale = Locale("es")
        val locale = Locale.getDefault()
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {

        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarError)
            )
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarSuccess))
        }
        snackBar.show()
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.progress_dialog)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

//    fun showNoInternetConnectionDialog() {
//        mNoInternetConnectionDialog = Dialog(this)
//        mNoInternetConnectionDialog.setContentView(R.layout.no_internet_connection_dialog)
//        mNoInternetConnectionDialog.findViewById<TextView>(R.id.no_internet_heading).text =
//            resources.getString(R.string.no_internet_connection)
//        mNoInternetConnectionDialog.findViewById<TextView>(R.id.no_internet_text).text =
//            resources.getString(R.string.check_your_internet_connection_and_try_again)
//        mNoInternetConnectionDialog.setCancelable(false)
//        mNoInternetConnectionDialog.setCanceledOnTouchOutside(false)
//        mNoInternetConnectionDialog.show()
//    }
//
//    fun hideNoInternetConnectionDialog() {
//        mNoInternetConnectionDialog = Dialog(this)
//        mNoInternetConnectionDialog.dismiss()
//    }


    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}