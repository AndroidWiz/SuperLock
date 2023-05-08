package com.sk.superlock.data.services

import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.sk.superlock.activity.LockActivity
import com.sk.superlock.util.Constants

//class LockService: Service() {
//
//    private val lockList = mutableListOf<String>()
//    private val activityManager by lazy {getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager}
//    private val lockReceiver by lazy { LockReceiver() }
//
//    override fun onCreate() {
//        super.onCreate()
//        registerReceiver(lockReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        lockList.addAll(listOf(Constants.LOCKED_APPS))
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    override fun onDestroy() {
//        unregisterReceiver(lockReceiver)
//        super.onDestroy()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    inner class LockReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == Intent.ACTION_SCREEN_ON) {
//                val packageName = activityManager.runningAppProcesses[0].processName
//
//                // add or remove the package name from the lockList based on the intent extras received
//                intent.getStringExtra("package_name")?.let { packageToRemove ->
//                    if (lockList.contains(packageToRemove)) {
//                        lockList.remove(packageToRemove)
//                    } else {
//                        lockList.add(packageToRemove)
//                    }
//                }
//
//                if (lockList.contains(packageName)) {
//                    // Launch the custom lock screen activity
//                    val customLockScreenIntent = Intent(context, LockActivity::class.java)
//                    customLockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(customLockScreenIntent)
//                }
//            }
//        }
//    }
//
//}

//    inner class LockReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == Intent.ACTION_SCREEN_ON) {
//                val packageName = activityManager.runningAppProcesses[0].processName
//                if (lockList.contains(packageName)) {
//                    // Launch the custom lock screen activity
//                    val customLockScreenIntent = Intent(context, LockActivity::class.java)
//                    customLockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(customLockScreenIntent)
//                }
//            }
//        }
//    }