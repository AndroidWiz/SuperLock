package com.sk.superlock.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sk.superlock.R
import com.sk.superlock.activity.LockActivity
import com.sk.superlock.data.model.Apps

class LockerService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "KeyAppLockChannel"
        private const val NOTIFICATION_ID = 200
    }

    private val appLaunchReceiver = AppLaunchReceiver()
    private val lockedApps = mutableListOf<Apps>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())

        // Register the appLaunchReceiver to listen for app launch events
        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addDataScheme("package")
        registerReceiver(appLaunchReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val packageName = intent?.getStringExtra("packageName")

        if (packageName != null) {
            // Lock or unlock the app based on the package name
            lockOrUnlockApp(packageName)
        }

        return START_STICKY
    }

    private fun lockOrUnlockApp(packageName: String) {
        val app = getAppByPackageName(packageName)

        if (app != null) {
            if (app.isLocked) {
                // App is already locked, so unlock it
                unlockApp(app)
            } else {
                // App is not locked, so lock it
                lockApp(app)
            }
        }
    }

    fun lockApp(app: Apps) {
        app.isLocked = true
        lockedApps.add(app)
        // You can store the locked app information or perform any additional logic here
    }

    fun unlockApp(app: Apps) {
        app.isLocked = false
        lockedApps.remove(app)
        // You can remove the unlocked app information or perform any additional logic here
    }

    private fun getAppByPackageName(packageName: String): Apps? {
        return lockedApps.find { it.packageName == packageName }
    }

    private fun createNotification(): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Key App Lock")
            .setContentText("Key App Lock is active")
            .setSmallIcon(R.drawable.ic_lock)
            .setColor(Color.BLUE)

        return notificationBuilder.build()
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel(): String {
        val channelId = NOTIFICATION_CHANNEL_ID
        val channelName = "Key App Lock"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channelId
    }

    private inner class AppLaunchReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val packageName = intent?.data?.schemeSpecificPart

            if (isAppLocked(packageName)) {
                // App is locked, show the PIN screen
                val pinIntent = Intent(context, LockActivity::class.java)
                pinIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                pinIntent.putExtra("packageName", packageName)
                context?.startActivity(pinIntent)
            } else {
                // App is not locked, allow it to launch
                val launchIntent = context?.packageManager?.getLaunchIntentForPackage(packageName!!)
                launchIntent?.let {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(it)
                }
            }
        }
    }

    fun isAppLocked(packageName: String?): Boolean {
        return packageName != null && lockedApps.any { it.packageName == packageName }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the appLaunchReceiver
        unregisterReceiver(appLaunchReceiver)
    }
}


/*
private inner class AppLaunchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.data?.schemeSpecificPart

        if (isAppLocked(packageName)) {
            // App is locked, show the PIN screen
            val pinIntent = Intent(context, LockActivity::class.java)
            pinIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            pinIntent.putExtra("packageName", packageName)
            context?.startActivity(pinIntent)
        }
    }
}*/
