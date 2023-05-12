package com.sk.superlock.services

import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder

class AppLockerService : Service() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    override fun onCreate() {
        super.onCreate()
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, AppLockerReceiver::class.java)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            LOCK_APPS_ACTION -> {
                val appIds = intent.getStringArrayListExtra(APP_IDS_EXTRA)?.toList()
                lockApps(appIds)
            }
            UNLOCK_APPS_ACTION -> {
                val appIds = intent.getStringArrayListExtra(APP_IDS_EXTRA)?.toList()
                unlockApps(appIds)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun lockApps(appIds: List<String>?) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            appIds?.forEach { appId ->
                val packageName = packageNameForAppId(appId)
                if (packageName != null) {
                    devicePolicyManager.setApplicationHidden(componentName, packageName, true)
                }
            }
        } else {
            requestDeviceAdmin()
        }
    }

    private fun unlockApps(appIds: List<String>?) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            appIds?.forEach { appId ->
                val packageName = packageNameForAppId(appId)
                if (packageName != null) {
                    devicePolicyManager.setApplicationHidden(componentName, packageName, false)
                }
            }
        } else {
            requestDeviceAdmin()
        }
    }

    private fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please enable device admin for this app.")
        startActivity(intent)
    }

    private fun packageNameForAppId(appId: String): String? {
        val pm = packageManager
        val intent = pm.getLaunchIntentForPackage(appId)
        return intent?.component?.packageName
    }

    companion object {
        const val LOCK_APPS_ACTION = "com.sk.superlock.LOCK_APPS"
        const val UNLOCK_APPS_ACTION = "com.sk.superlock.UNLOCK_APPS"
        const val APP_IDS_EXTRA = "appIds"
    }
}