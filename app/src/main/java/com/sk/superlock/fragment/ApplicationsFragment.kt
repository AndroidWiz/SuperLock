package com.sk.superlock.fragment

import android.annotation.SuppressLint
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
import com.sk.superlock.data.services.AppLockerService
import com.sk.superlock.databinding.FragmentApplicationsBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.PrefManager

class ApplicationsFragment : Fragment(), AllAppListAdapter.OnAppAddedListener {

    private lateinit var binding: FragmentApplicationsBinding
    private lateinit var filteredApList: MutableList<String>
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // search view
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                // handle query text changes
                filteredApList = mutableListOf()
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
            PrefManager(requireContext()).addLockedApp(app.appPackageName)

            // start the service to check if the added app should be locked
            val intent = Intent(requireContext(), AppLockerService::class.java)
            intent.putExtra(AppLockerService.PACKAGE_NAME, app.appPackageName)
            requireContext().startService(intent)

            appsAdapter.notifyDataSetChanged()
        }
    }

    override fun onAppRemoved(app: Applications) {
        addedAppList.remove(app)
        PrefManager(requireContext()).removeLockedApp(app.appPackageName)

        appsAdapter.notifyDataSetChanged()
    }
}


//override fun onAppAdded(app: Applications) {
//    if (!ApplicationsFragment.addedAppList.contains(app)) {
//        ApplicationsFragment.addedAppList.add(app)
//        PrefManager(requireContext()).addLockedApp(app.appPackageName)
//
//        // add the package name to the lockList in the LockService
//        val lockServiceIntent = Intent(requireContext(), LockService::class.java)
//        lockServiceIntent.putExtra("package_name", app.appPackageName)
//        requireContext().startService(lockServiceIntent)
//
//        appsAdapter.notifyDataSetChanged()
//    }
//}
//
//override fun onAppRemoved(app: Applications) {
//    ApplicationsFragment.addedAppList.remove(app)
//    PrefManager(requireContext()).removeLockedApp(app.appPackageName)
//
//    // remove the package name from the lockList in the LockService
//    val lockServiceIntent = Intent(requireContext(), LockService::class.java)
//    lockServiceIntent.putExtra("package_name", app.appPackageName)
//    requireContext().stopService(lockServiceIntent)
//
//    appsAdapter.notifyDataSetChanged()
//}

//        if (!addedAppList.contains(app)) {
//            addedAppList.add(app)
//            PrefManager(requireContext()).addLockedApp(app.appPackageName)
//            appsAdapter.notifyDataSetChanged()
//        }

//        addedAppList.remove(app)
//        PrefManager(requireContext()).removeLockedApp(app.appPackageName)
//        appsAdapter.notifyDataSetChanged()