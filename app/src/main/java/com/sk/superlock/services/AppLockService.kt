package com.sk.superlock.services

import android.annotation.SuppressLint
import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.sk.superlock.R
import com.sk.superlock.activity.LockActivity
import com.sk.superlock.util.Constants
import java.util.*

class AppLockService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "KeyAppLockChannel"
        private const val NOTIFICATION_ID = 200
    }

    private lateinit var mSharedPrefs: SharedPreferences
    private lateinit var appLaunchReceiver: BroadcastReceiver
    private lateinit var apps: MutableSet<String>
    private var foregroundAppPackageName: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mSharedPrefs = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        apps = mSharedPrefs.getStringSet(Constants.LOCKED_APPS, mutableSetOf())!!

        startForeground(NOTIFICATION_ID, createNotification())

        // register the appLaunchReceiver to listen for app launch events
        appLaunchReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val packageName = intent?.data?.schemeSpecificPart

                if (apps.contains(packageName)) {
                    val lockActivity = Intent(this@AppLockService, LockActivity::class.java)
                    lockActivity.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
                    startActivity(lockActivity)
                }
            }
        }

//        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addDataScheme("package")
        registerReceiver(appLaunchReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(appLaunchReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        apps = mSharedPrefs.getStringSet(Constants.LOCKED_APPS, mutableSetOf())!!

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val packageName = intent?.data?.schemeSpecificPart

                getForegroundAppPackageName { foregroundPackageName ->
                    foregroundAppPackageName = foregroundPackageName

                    if (apps.contains(foregroundPackageName)) {
                        val lockActivity = Intent(this@AppLockService, LockActivity::class.java)
                        lockActivity.putExtra(Intent.EXTRA_PACKAGE_NAME, foregroundPackageName)
                        lockActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // flag to start the activity outside of a task
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
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channelId
    }

    private fun getForegroundAppPackageName(callback: (String) -> Unit) {
        val usageStatsManager = applicationContext.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val handler = Handler(Looper.getMainLooper())
        val delayMillis = 100L

        val checkPermissionRunnable = object : Runnable {
            override fun run() {
                if (checkUsageAccessPermission()) {
                    val currentTime = System.currentTimeMillis()
                    val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 10, currentTime)

                    var packageName = ""
                    if (stats != null) {
                        val sortedStats = TreeMap<Long, UsageStats>()
                        for (usageStats in stats) {
                            sortedStats[usageStats.lastTimeUsed] = usageStats
                        }

                        if (sortedStats.isNotEmpty()) {
                            packageName = sortedStats[sortedStats.lastKey()]?.packageName ?: ""
                        }
                    }
                    callback(packageName)
                }
                handler.postDelayed(this, delayMillis)
            }
        }
        handler.post(checkPermissionRunnable)
    }

    private fun checkUsageAccessPermission(): Boolean {
        val packageName = applicationContext.packageName
        val packageManager = applicationContext.packageManager

        val appOpsClass = try {
            Class.forName(AppOpsManager::class.java.name)
        } catch (e: ClassNotFoundException) {
            return false
        }

        return try {
            val appOpsManager = applicationContext.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            val mode = appOpsManager?.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                packageManager.getApplicationInfo(packageName, 0).uid,
                packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

/*private fun checkUsageAccessPermission(): Boolean {
        val packageName = applicationContext.packageName
        val packageManager = applicationContext.packageManager

        val appOpsClass = try {
            Class.forName(AppOpsManager::class.java.name)
        } catch (e: ClassNotFoundException) {
            return false
        }

        return try {
            val appOpsManager = applicationContext.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            val mode = appOpsManager?.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, packageManager.getApplicationInfo(packageName, 0).uid, packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }*/

}