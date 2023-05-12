package com.sk.superlock.fragment

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.sk.superlock.R

class LockCheckFragment : Fragment() {
    private lateinit var packageManager: PackageManager
    private val lockedApps = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the package manager.
        packageManager = requireContext().packageManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the list of apps.
        val listView = view.findViewById<ListView>(R.id.app_list)
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        for (app in packageManager.getInstalledPackages(0)) {
            adapter.add(app.packageName)
        }
        listView.adapter = adapter

        // Set up the click listener for the list items.
        listView.setOnItemClickListener { _, _, position, _ ->
            // Get the package name of the app.
            val packageName = adapter.getItem(position)

            // Lock or unlock the app.
            if (lockedApps.contains(packageName)) {
                lockedApps.remove(packageName)
            } else {
                lockedApps.add(packageName.toString())
            }

            // Update the list of apps.
            adapter.clear()
            for (app in packageManager.getInstalledPackages(0)) {
                if (lockedApps.contains(app.packageName)) {
                    adapter.add(app.packageName)
                }
            }
            listView.adapter = adapter
        }
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