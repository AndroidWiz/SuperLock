package com.sk.superlock.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.FirebaseApp
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityMainBinding
import com.sk.superlock.fragment.*
import com.sk.superlock.util.Constants
import com.sk.superlock.util.CustomTextView
import com.sk.superlock.util.CustomTextViewBold
import com.sk.superlock.util.GlideLoader

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialising view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        // navigation drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open_drawer, R.string.close_drawer)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()

        // action bar
        val actionBar = supportActionBar
        if (actionBar != null) {
            val title = resources.getString(R.string.app_name)
            val spannableString = SpannableString(title)
            spannableString.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(this, R.color.white)), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            actionBar.title = spannableString
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        // show user details to the navigation header
        val sharedPrefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPrefs.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val email = sharedPrefs.getString(Constants.LOGGED_IN_USER_EMAIL, "")!!
        val profileImage = sharedPrefs.getString(Constants.LOGGED_IN_USER_IMAGE, "")!!

        val navView = binding.navBarView
        val header: View = navView.getHeaderView(0)
        val hUsername = header.findViewById<CustomTextViewBold>(R.id.tv_username_nav_header)
        val hEmail = header.findViewById<CustomTextView>(R.id.tv_user_email_nav_header)
        val hProfilePic = header.findViewById<ShapeableImageView>(R.id.iv_user_img_nav_header)
        hUsername.text = username
        hEmail.text = email
        GlideLoader(this@MainActivity).loadUserPicture(profileImage, hProfilePic)

        setUpNavBar()
        setFragment(HomeFragment())
        showBottomSheetDialog()

        // button configuration
        binding.btnNavConfiguration.setOnClickListener {
            startActivity(Intent(this, ConfigurationActivity::class.java))
        }
    }

    // side navigation bar
    private fun setUpNavBar() {
        binding.navBarView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_home) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(HomeFragment())
                }
                R.id.nav_security -> {
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_security) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(SecurityFragment())
                }
                R.id.nav_all_applications -> {
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_applications) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(ApplicationsFragment())
                }
                R.id.nav_safe_zones -> {
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_safe_zones) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(SafeZonesFragment())
                }
                R.id.nav_about_us -> {
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_about_us) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(AboutUsFragment())
                }
            }
            true
        }
    }

    // setup custom bottom sheet dialog
    private fun showBottomSheetDialog(){
        val bottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        val dialogView = layoutInflater.inflate(R.layout.permission_bottom_sheet, null)
        bottomSheetDialog.setContentView(dialogView)

        val cbProtectedApps = dialogView.findViewById<AppCompatCheckBox>(R.id.cb_protected_apps)
        val cbAutoStart = dialogView.findViewById<AppCompatCheckBox>(R.id.cb_auto_start)

        //TODO:work on updating the preferences to system settings

        bottomSheetDialog.show()
    }

    // set default fragment
    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
        // close drawer
        binding.root.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        setLocale()
        super.onResume()
    }
}