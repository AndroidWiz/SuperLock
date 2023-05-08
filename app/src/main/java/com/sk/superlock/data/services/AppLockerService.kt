package com.sk.superlock.data.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sk.superlock.activity.LockActivity
import com.sk.superlock.util.PrefManager

//class AppLockerService : AccessibilityService() {
//    companion object {
//        private const val TAG = "AppLockerService"
//    }
//
//    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            val packageName = event.packageName?.toString()
//            if (packageName != null && PrefManager(applicationContext).isAppLocked(packageName)) {
//                val lockScreenIntent = Intent(applicationContext, LockActivity::class.java)
//                lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(lockScreenIntent)
//                performGlobalAction(GLOBAL_ACTION_BACK)
//            }
//        }
//    }
//
//    override fun onInterrupt() {
//        // handle accessibility service interruption
//        Log.d(TAG, "AppLockerService interrupted")
//    }
//}

/* fun isAppLocked(packageName: String): Boolean {
     val preferences = applicationContext?.getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE)
     val lockedApps = preferences?.getStringSet("lockedApps", emptySet())
     return lockedApps!!.contains(packageName)
 }

 fun addLockedApp(packageName: String) {
     val preferences = applicationContext?.getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE)
     val lockedApps = preferences?.getStringSet("lockedApps", mutableSetOf()) ?: mutableSetOf()
     lockedApps.add(packageName)
     preferences?.edit()?.putStringSet("lockedApps", lockedApps)?.apply()
 }

 fun removeLockedApp(packageName: String) {
     val preferences = applicationContext?.getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE)
     val lockedApps = preferences?.getStringSet("lockedApps", mutableSetOf()) ?: mutableSetOf()
     lockedApps.remove(packageName)
     preferences?.edit()?.putStringSet("lockedApps", lockedApps)?.apply()
 }*/