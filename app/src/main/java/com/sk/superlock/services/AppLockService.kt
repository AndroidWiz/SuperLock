package com.sk.superlock.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sk.superlock.R
import com.sk.superlock.activity.LockActivity
import com.sk.superlock.util.Constants

class AppLockService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "KeyAppLockChannel"
        private const val NOTIFICATION_ID = 200
    }

    private lateinit var mSharedPrefs: SharedPreferences

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mSharedPrefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)

        startForeground(NOTIFICATION_ID, createNotification())

        // Register the appLaunchReceiver to listen for app launch events
        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addDataScheme("package")
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apps = mSharedPrefs.getStringSet(Constants.LOCKED_APPS, mutableSetOf())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val packageName = intent?.getStringExtra(Intent.EXTRA_PACKAGE_NAME)

                if (apps != null) {
                    if(apps.contains(packageName)){
                        val lockActivity = Intent(this@AppLockService, LockActivity::class.java)
                        lockActivity.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
                        startActivity(lockActivity)
                    }
                }
            }
        }

        registerReceiver(receiver, IntentFilter(Intent.ACTION_PACKAGE_ADDED))
        registerReceiver(receiver, IntentFilter(Intent.ACTION_PACKAGE_REMOVED))

        return START_STICKY
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
}