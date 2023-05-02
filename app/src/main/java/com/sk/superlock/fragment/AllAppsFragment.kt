package com.sk.superlock.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sk.superlock.adapter.AllAppListAdapter
import com.sk.superlock.data.model.Applications
import com.sk.superlock.databinding.FragmentAllAppsBinding
import com.sk.superlock.util.Constants
import com.sk.superlock.util.PrefManager

@Suppress("DEPRECATION")
class AllAppsFragment : Fragment(), AllAppListAdapter.OnAppAddedListener {

    private lateinit var binding: FragmentAllAppsBinding
    private lateinit var appsAdapter: AllAppListAdapter
    lateinit var sharedPref: PrefManager

    companion object {
        val TAG: String = "AllAppsFragment"
        var allAppList: MutableList<Applications> = mutableListOf()
        var addedAppList: MutableList<Applications> = mutableListOf()
        var allAppsListSize: Int? = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAppsBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPref = PrefManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val appIcon = activity.loadIcon(packageManager)
            apps.add(Applications(appName, appIcon))
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
//        appsAdapter.notifyDataSetChanged()
    }

    override fun onAppAdded(app: Applications) {
        if (!addedAppList.contains(app)) {
            addedAppList.add(app)
            appsAdapter.notifyDataSetChanged()
        }
    }

    override fun onAppRemoved(app: Applications) {
        addedAppList.remove(app)
        appsAdapter.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs =
            requireContext().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val addedAppListString = sharedPrefs.getString(Constants.ADDED_APP_LIST, null)
        if (addedAppListString != null) {
            addedAppList = Gson().fromJson(
                addedAppListString,
                object : TypeToken<List<Applications>>() {}.type
            )
        }
    }


    override fun onPause() {
        super.onPause()
        // save the added app list to SharedPreferences
        val sharedPrefs =
            requireContext().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(Constants.ADDED_APP_LIST, Gson().toJson(addedAppList))
        editor.apply()
    }
}
