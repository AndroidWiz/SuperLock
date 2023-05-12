package com.sk.superlock.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Get the new lock status.
        val newLockStatus = intent.getBooleanExtra("lockStatus", false)

        // Update the lock status of the apps.
        LockService().updateLockedApps(newLockStatus)
    }

}