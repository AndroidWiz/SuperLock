package com.sk.superlock.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityMainBinding
import com.sk.superlock.fragment.*

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialising view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open_drawer, R.string.close_drawer)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpNavBar()
        setFragment(HomeFragment())

        // button configuration
        binding.btnNavConfiguration.setOnClickListener {
            startActivity(Intent(this, ConfigurationActivity::class.java))
        }

    }

    // side navigation bar
    private fun setUpNavBar(){
        binding.navBarView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home ->{
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_home) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(HomeFragment())
                }
                R.id.nav_security ->{
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_security) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(SecurityFragment())
                }
                R.id.nav_all_applications ->{
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_applications) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(ApplicationsFragment())
                }
                R.id.nav_safe_zones ->{
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_safe_zones) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(SafeZonesFragment())
                }
                R.id.nav_about_us ->{
                    Toast.makeText(this@MainActivity, resources.getString(R.string.nav_title_about_us) + resources.getString(R.string.clicked), Toast.LENGTH_SHORT).show()
                    setFragment(AboutUsFragment())
                }
            }
            true
        }
    }

    // set default fragment
    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        setLocale()
        super.onResume()
    }
}