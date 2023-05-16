package com.sk.superlock.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.services.LockService

class LockCheckFragment : Fragment() {
    private lateinit var lockServiceIntent: Intent
    private val lockedPackages: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockServiceIntent = Intent(requireContext(), LockService::class.java)

        requireContext().startService(lockServiceIntent)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lock_check, container, false)

        val appList = view.findViewById<ListView>(R.id.app_list)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1 , getInstalledApps())
        appList.adapter = adapter

        appList.setOnItemClickListener { _, _, position, _ ->
            val app = adapter.getItem(position)
            if (app != null) {
                if (lockedPackages.contains(app.packageName)) {
                    // App is already locked, remove from the list of locked packages
                    lockedPackages.remove(app.packageName)
                    Toast.makeText(requireContext(), "App unlocked", Toast.LENGTH_SHORT).show()
                } else {
                    // App is not yet locked, add to the list of locked packages
                    lockedPackages.add(app.packageName)
                    Toast.makeText(requireContext(), "App locked", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun getInstalledApps(): List<ApplicationInfo> {
        val pm = requireContext().packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { !isSystemApp(it) }
            .sortedBy { it.loadLabel(pm).toString() }
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    override fun onPause() {
        super.onPause()
        // Send the selected locked packages to the LockService before the fragment is paused
        lockServiceIntent.action = LockService.ACTION_LOCK_APP
        lockServiceIntent.putExtra(LockService.EXTRA_PACKAGE_NAME, lockedPackages.toTypedArray())
        requireContext().startService(lockServiceIntent)
    }

    // Get the PIN.
    private fun getPin(): String {
        // TODO: Get the PIN from the user.
        return "1234"
    }


}

data class LockedApp(val packageName: String, var isLocked: Boolean)

data class App(val appName: String, val packageName: String)

class PinDialog(context: Context) : Dialog(context) {

    private lateinit var pinEditText: EditText
    private var onPinEnteredListener: ((pin: String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_dialog)

        pinEditText = findViewById(R.id.pin_edit_text)

        val button = findViewById<Button>(R.id.submit_button)
        button.setOnClickListener {
            val pin = pinEditText.text.toString()
            onPinEnteredListener?.invoke(pin)
            dismiss()
        }
    }

    fun setOnPinEnteredListener(listener: (pin: String) -> Unit) {
        onPinEnteredListener = listener
    }
}