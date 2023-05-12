package com.sk.superlock.fragment

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.superlock.adapter.AllAppListAdapter
import com.sk.superlock.data.model.Applications
import com.sk.superlock.databinding.FragmentApplicationsBinding
import com.sk.superlock.services.AppLockerReceiver
import com.sk.superlock.services.AppLockerService
import com.sk.superlock.services.AppLockerService.Companion.APP_IDS_EXTRA
import com.sk.superlock.services.AppLockerService.Companion.LOCK_APPS_ACTION
import com.sk.superlock.services.AppLockerService.Companion.UNLOCK_APPS_ACTION
import com.sk.superlock.util.Constants
import com.sk.superlock.util.PrefManager
import java.util.*

class ApplicationsFragment : Fragment(), AllAppListAdapter.OnAppAddedListener {

    private lateinit var binding: FragmentApplicationsBinding
    private lateinit var filteredApList: MutableList<Applications>
    private lateinit var appsAdapter: AllAppListAdapter
    private lateinit var sharedPref: PrefManager

    companion object {
        const val TAG: String = "ApplicationsFragment"
        var allAppList: MutableList<Applications> = mutableListOf()
        var addedAppList: MutableList<Applications> = mutableListOf()
        var allAppsListSize: Int? = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplicationsBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPref = PrefManager(requireContext())
        requestAdminAccess(requireContext())


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // search view
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredApList = mutableListOf()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for(app in allAppList){
                        if(app.appName.lowercase().contains(userInput)) filteredApList.add(app)
                    }
                    appsAdapter.filterList(filteredApList)
                }
                return true
            }

        })

        allAppList = getAllInstalledApps()
        setupRecyclerView(allAppList)
    }

    // list of all available apps in phone
    @SuppressLint("QueryPermissionsNeeded")
    private fun getAllInstalledApps(): MutableList<Applications> {
        val packageManager = requireContext().packageManager
        val apps = mutableListOf<Applications>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = packageManager.queryIntentActivities(intent, 0)
        for (activity in activities) {
            val appName = activity.loadLabel(packageManager).toString()
            val appPackageName = activity.activityInfo.packageName
            val appIcon = activity.loadIcon(packageManager)
            val locked = PrefManager(requireContext()).isAppLocked(appPackageName)
            apps.add(Applications(appName, appPackageName, appIcon, locked))
        }
        apps.sortBy { it.appName }

        allAppsListSize = apps.size
        sharedPref.saveInt(Constants.AVAILABLE_APPS_SIZE, allAppsListSize!!)
        Log.d(TAG, "allAppsListSize: $allAppsListSize")
        return apps
    }

    // set up recyclerview for list of application
    private fun setupRecyclerView(appList: MutableList<Applications>) {
        binding.rvAppList.setHasFixedSize(true)
        binding.rvAppList.setItemViewCacheSize(50)
        binding.rvAppList.layoutManager = LinearLayoutManager(requireContext())
        appsAdapter = AllAppListAdapter(requireContext(), appList, addedAppList, this)
        binding.rvAppList.adapter = appsAdapter
    }

    override fun onAppAdded(app: Applications) {
        if (!addedAppList.contains(app)) {
            addedAppList.add(app)

            // start the service to lock the app
            val appIds = arrayListOf(app.appPackageName)
            val intent = Intent(requireContext(), AppLockerService::class.java)
            intent.action = LOCK_APPS_ACTION
            intent.putStringArrayListExtra(APP_IDS_EXTRA, appIds)
            requireContext().startService(intent)

            appsAdapter.notifyDataSetChanged()
        }
    }

    override fun onAppRemoved(app: Applications) {
        addedAppList.remove(app)

        appsAdapter.notifyDataSetChanged()
    }

    private fun requestAdminAccess(context: Context) {
        val componentName = ComponentName(context, AppLockerReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please activate device administrator for this app")
        startActivityForResult(intent, 103)
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                LOCK_APPS_ACTION -> {
                    val appIds = intent.getStringArrayListExtra(APP_IDS_EXTRA)
                    // TODO: Update the UI to show that the apps are locked
                }
                UNLOCK_APPS_ACTION -> {
                    val appIds = intent.getStringArrayListExtra(APP_IDS_EXTRA)
                    // TODO: Update the UI to show that the apps are unlocked
                }
            }
        }
    }

}


//        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)