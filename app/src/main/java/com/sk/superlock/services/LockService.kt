package com.sk.superlock.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.sk.superlock.fragment.LockedApp

class LockService : Service() {

    private lateinit var lockedApps: MutableList<LockedApp>

    // Register a broadcast receiver to listen for changes in the lock status of the apps.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Update the lock status of the apps based on the new lock status.
            updateLockedApps(intent.getBooleanExtra("lockStatus", false))
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize the list of locked apps.
        lockedApps = mutableListOf()
        registerReceiver(receiver, IntentFilter(Intent.ACTION_USER_PRESENT))
    }

    override fun onDestroy() {
        // Unregister the broadcast receiver.
        unregisterReceiver(this.receiver)

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // Update the lock status of the apps.
    fun updateLockedApps(lockStatus: Boolean) {
        // Get the list of all the apps on the device.
        val packageManager = applicationContext.packageManager
        val apps = packageManager.getInstalledApplications(0)

        // Update the lock status of the apps.
        for (app in apps) {
            val packageName = app.packageName
            val lockedApp = lockedApps.find { it.packageName == packageName }
            lockedApp?.isLocked = lockStatus
        }
    }

}