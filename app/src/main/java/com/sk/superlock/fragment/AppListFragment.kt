package com.sk.superlock.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.superlock.adapter.AppListAdapter
import com.sk.superlock.data.model.Apps
import com.sk.superlock.databinding.FragmentAppListBinding
import com.sk.superlock.services.AppLockService
import com.sk.superlock.util.Constants
import com.sk.superlock.util.PrefManager

class AppListFragment : Fragment(), AppListAdapter.OnAppAddedListener {

    private lateinit var binding: FragmentAppListBinding
    private lateinit var appsAdapter: AppListAdapter
    private lateinit var sharedPref: PrefManager

    val appLockService: AppLockService = AppLockService()
    private lateinit var lockedApps: MutableSet<String>

    companion object {
        const val TAG: String = "AppListFragment"
        var appList: MutableList<Apps> = mutableListOf()
        var addedAppList: MutableList<Apps> = mutableListOf()
        var allAppsListSize: Int? = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppListBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPref = PrefManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lockedApps = sharedPref.getLockedApps().toMutableSet()

        appList = getAllInstalledApps()
        updateAppListLockStatus()
        setupRecyclerView(appList)
    }

    private fun getAllInstalledApps(): MutableList<Apps> {
        val packageManager = requireContext().packageManager
        val apps = mutableListOf<Apps>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = packageManager.queryIntentActivities(intent, 0)
        for (activity in activities) {
            val appName = activity.loadLabel(packageManager).toString()
            val appPackageName = activity.activityInfo.packageName
            val appIcon = activity.loadIcon(packageManager)
            val locked = PrefManager(requireContext()).isAppLocked(appPackageName)
            apps.add(Apps(icon = appIcon, name = appName, packageName = appPackageName))
        }
        apps.sortBy { it.name }

        allAppsListSize = apps.size
        sharedPref.saveInt(Constants.AVAILABLE_APPS_SIZE, allAppsListSize!!)
        Log.d(TAG, "allAppsListSize: $allAppsListSize")

        return apps
    }

    private fun updateAppListLockStatus() {
        for (app in appList) {
            app.isLocked = lockedApps.contains(app.packageName)
        }
    }

    private fun setupRecyclerView(appList: MutableList<Apps>) {
        binding.rvAppList.setHasFixedSize(true)
        binding.rvAppList.setItemViewCacheSize(50)
        binding.rvAppList.layoutManager = LinearLayoutManager(requireContext())
        appsAdapter = AppListAdapter(requireContext(), appList, addedAppList, this)
        binding.rvAppList.adapter = appsAdapter
        appsAdapter.notifyDataSetChanged()
    }

    override fun onAppAdded(app: Apps) {
        addedAppList.add(app)
        lockedApps.add(app.packageName)
        sharedPref.saveLockedApps(lockedApps)
        app.isLocked = true
        appsAdapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${app.name} locked", Toast.LENGTH_LONG).show()
    }

    override fun onAppRemoved(app: Apps) {
        addedAppList.remove(app)
        lockedApps.remove(app.packageName)
        sharedPref.saveLockedApps(lockedApps)
        app.isLocked = false
        appsAdapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${app.name} removed", Toast.LENGTH_LONG).show()
    }


}

/*   override fun onAppAdded(app: Apps) {
        addedAppList.add(app)
        lockerService.lockApp(app)
        appsAdapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${app.name} locked", Toast.LENGTH_LONG).show()
    }

    override fun onAppRemoved(app: Apps) {
        addedAppList.remove(app)
        lockerService.unlockApp(app)
        appsAdapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${app.name} removed", Toast.LENGTH_LONG).show()
    }*/