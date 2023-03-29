package com.sk.superlock.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sk.superlock.adapter.AllAppListAdapter
import com.sk.superlock.databinding.FragmentAllAppsBinding
import com.sk.superlock.model.Applications
import com.sk.superlock.util.Constants

class AllAppsFragment : Fragment(), AllAppListAdapter.OnAppAddedListener {

    private lateinit var binding: FragmentAllAppsBinding

    //    private var allAppList: MutableList<Applications> = mutableListOf()
    private lateinit var appsAdapter: AllAppListAdapter
//    private var addedAppList: MutableList<Applications> = mutableListOf()

    companion object {
        var allAppList: MutableList<Applications> = mutableListOf()
        var addedAppList: MutableList<Applications> = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAppsBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView(allAppList)

        return view
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

    override fun onResume() {
        super.onResume()

        allAppList.add(Applications("WhatsApp"))
        allAppList.add(Applications("Twitter"))
        allAppList.add(Applications("Facebook"))
        allAppList.add(Applications("Messenger"))
        allAppList.add(Applications("WeChat"))
        allAppList.add(Applications("Fiverr"))
        allAppList.add(Applications("Upwork"))

        appsAdapter.notifyDataSetChanged()
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
