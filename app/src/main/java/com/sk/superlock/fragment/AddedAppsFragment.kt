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
import com.sk.superlock.adapter.AddedAppListAdapter
import com.sk.superlock.databinding.FragmentAddedAppsBinding
import com.sk.superlock.model.Applications
import com.sk.superlock.util.Constants

class AddedAppsFragment : Fragment() {

    private lateinit var binding: FragmentAddedAppsBinding
    //    private var addedAppList: MutableList<Applications> = mutableListOf()
    private lateinit var appsAdapter: AddedAppListAdapter

    companion object {
        var addedAppList: MutableList<Applications> = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddedAppsBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPrefs =
            requireContext().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val addedAppListString = sharedPrefs.getString(Constants.ADDED_APP_LIST, null)
        if (addedAppListString != null) {
            addedAppList = Gson().fromJson(
                addedAppListString,
                object : TypeToken<List<Applications>>() {}.type
            )
        }

        setupRecyclerView(addedAppList)

        return view
    }

    // set up recyclerview for list of application
    private fun setupRecyclerView(appList: MutableList<Applications>) {
        binding.rvAddedAppList.setHasFixedSize(true)
        binding.rvAddedAppList.setItemViewCacheSize(50)
        binding.rvAddedAppList.layoutManager = LinearLayoutManager(requireContext())
        appsAdapter = AddedAppListAdapter(requireContext(), appList)
        binding.rvAddedAppList.adapter = appsAdapter
        appsAdapter.notifyDataSetChanged()
    }

}