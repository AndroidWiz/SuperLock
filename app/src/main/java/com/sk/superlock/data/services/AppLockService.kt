package com.sk.superlock.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sk.superlock.activity.LockActivity
import com.sk.superlock.util.PrefManager

class AppLockerService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val packageName = intent?.getStringExtra(PACKAGE_NAME)
        if (packageName != null) {
            if (PrefManager(applicationContext).isAppLocked(packageName)) {
                val lockIntent = Intent(this, LockActivity::class.java)
                lockIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                lockIntent.putExtra(PACKAGE_NAME, packageName)
                startActivity(lockIntent)
            }
        }
        return START_NOT_STICKY
    }

    companion object {
        const val PACKAGE_NAME = "package_name"
    }
}
