package com.sk.superlock.data.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppLockerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            AppLockerService.LOCK_APPS_ACTION -> {
                val appIds = intent.getStringArrayListExtra(AppLockerService.APP_IDS_EXTRA)?.toList()
                startLockService(context, appIds)
            }
            AppLockerService.UNLOCK_APPS_ACTION -> {
                val appIds = intent.getStringArrayListExtra(AppLockerService.APP_IDS_EXTRA)?.toList()
                startUnlockService(context, appIds)
            }
        }
    }

    private fun startLockService(context: Context?, appIds: List<String>?) {
        val lockIntent = Intent(context, AppLockerService::class.java)
        lockIntent.action = AppLockerService.LOCK_APPS_ACTION
        lockIntent.putStringArrayListExtra(AppLockerService.APP_IDS_EXTRA,
            appIds?.let { ArrayList(it) })
        context?.startService(lockIntent)
    }

    private fun startUnlockService(context: Context?, appIds: List<String>?) {
        val unlockIntent = Intent(context, AppLockerService::class.java)
        unlockIntent.action = AppLockerService.UNLOCK_APPS_ACTION
        unlockIntent.putStringArrayListExtra(AppLockerService.APP_IDS_EXTRA,
            appIds?.let { ArrayList(it) })
        context?.startService(unlockIntent)
    }
}

