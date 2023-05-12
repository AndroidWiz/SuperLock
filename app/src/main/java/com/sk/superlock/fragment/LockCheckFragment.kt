package com.sk.superlock.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.services.LockReceiver
import com.sk.superlock.services.LockService

class LockCheckFragment : Fragment() {

    private lateinit var lockedApps: MutableList<LockedApp>
    private lateinit var lockService: LockService
    private lateinit var lockReceiver: LockReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the list of locked apps.
        lockedApps = mutableListOf()

        // Initialize the lock service.
        lockService = LockService()

        // Initialize the lock receiver.
        lockReceiver = LockReceiver()
    }

    override fun onStart() {
        super.onStart()

        // Start the lock service.
        requireContext().startService(Intent(requireContext(), LockService::class.java))

        // Register the lock receiver.
        requireContext().registerReceiver(lockReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
    }

    override fun onStop() {
        super.onStop()

        // Stop the lock service.
        requireContext().stopService(Intent(requireContext(), LockService::class.java))

        // Unregister the lock receiver.
        requireContext().unregisterReceiver(lockReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the app list.
        val appList = view.findViewById<ListView>(R.id.app_list)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, getAppList())
        appList.adapter = adapter

        // Set up the click listener for the app list.
        appList.setOnItemClickListener { _, _, position, _ ->
            val app = adapter.getItem(position) as App
            val packageName = app.packageName
            if (lockedApps.any { it.packageName == packageName && it.isLocked }) {
                // The app is locked, ask the user to enter the PIN to unlock it.
                showPinDialog()
            } else {
                // The app is not locked, launch it.
                lockApp(packageName)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("package:$packageName")))
            }
        }
    }

    // Get the list of all the apps on the device.
    private fun getAppList(): List<App> {
        val packageManager = requireActivity().packageManager
        val apps = packageManager.getInstalledApplications(0)
        val appList = mutableListOf<App>()
        for (app in apps) {
            val appName = app.loadLabel(packageManager).toString()
            val packageName = app.packageName
            appList.add(App(appName, packageName))
        }
        return appList
    }

    // Show the PIN dialog.
    private fun showPinDialog() {
        val dialog = PinDialog(requireContext())
        dialog.setOnPinEnteredListener { pin ->
            if (pin == getPin()) {
                // The PIN is correct, unlock the app.
                unlockApp(dialog.packageName)
            } else {
                // The PIN is incorrect, show an error message.
                Toast.makeText(requireContext(), "PIN is incorrect.", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    // Get the PIN.
    private fun getPin(): String {
        // TODO: Get the PIN from the user.
        return "1234"
    }

    // Lock an app.
    private fun lockApp(packageName: String) {
        val lockedApp = LockedApp(packageName, true)
        lockedApps.add(lockedApp)
        lockService.updateLockedApps(lockedApps)
    }

    // Unlock an app.
    private fun unlockApp(packageName: String) {
        val lockedApp = lockedApps.find { it.packageName == packageName }
        lockedApp?.isLocked = false
        lockService.updateLockedApps(lockedApps)
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