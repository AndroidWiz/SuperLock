package com.sk.superlock.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.sk.superlock.activity.LockActivity

class LockService : Service() {

    companion object{
        const val EXTRA_PACKAGE_NAME = "com.sk.superlock.services"
        const val ACTION_LOCK_APP = "com.sk.superlock.ACTION_LOCK_APP"
        const val ACTION_UNLOCK_APP = "com.sk.superlock.ACTION_UNLOCK_APP"
    }

    // Define a mutable set to hold package names of locked apps
    private val lockedApps: MutableSet<String> = mutableSetOf()
    private val receiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val packageName = intent?.component?.packageName

            if(isAppLocked(packageName)){
                // app locked, show PIN screen
                val pinIntent = Intent(context, LockActivity::class.java)
                pinIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                pinIntent.putExtra("packageName", packageName)
                context?.startActivity(pinIntent)
                abortBroadcast()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            // Handle the ACTION_LOCK_APP intent
            ACTION_LOCK_APP -> {
                val packageNames = intent.getStringArrayExtra(EXTRA_PACKAGE_NAME)
                if (packageNames != null) {
                    lockedApps.addAll(packageNames)
                }
            }

            // Handle other intents (if any)

        }

        // Register a broadcast receiver to listen for app launches
        val filter = IntentFilter(Intent.ACTION_MAIN)
        filter.addCategory(Intent.CATEGORY_LAUNCHER)

        registerReceiver(receiver, filter)

        return super.onStartCommand(intent, flags, startId)
    }

    fun isAppLocked(packageName: String?): Boolean {
        return packageName != null && lockedApps.contains(packageName)
    }

    override fun onDestroy() {
        // Unregister the broadcast receiver when the service is destroyed
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}

/*class LockService : Service() {

    companion object{
        const val EXTRA_PACKAGE_NAME = "com.sk.superlock.services"
        const val ACTION_LOCK_APP = "com.sk.superlock.ACTION_LOCK_APP"
        const val ACTION_UNLOCK_APP = "com.sk.superlock.ACTION_UNLOCK_APP"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_MAIN)
        filter.addCategory(Intent.CATEGORY_LAUNCHER)

        val receiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val packageName = intent?.component?.packageName

                if(isAppLocked(packageName)){
                    // app locked, show PIN screen
                    val pinIntent = Intent(context, LockActivity::class.java)
                    pinIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    pinIntent.putExtra("packageName", packageName)
                    context?.startActivity(pinIntent)
                    abortBroadcast()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun isAppLocked(packageName: String?): Boolean{
        // check if the given app package name is locked or not
        // return true if locked

    }

}*/
