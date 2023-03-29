package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.superlock.adapter.AvailableAppListAdapter
import com.sk.superlock.databinding.FragmentAddedAppsBinding

class AddedAppsFragment : Fragment() {

    private lateinit var binding: FragmentAddedAppsBinding
    private var addedAppList : MutableList<String> = mutableListOf()
    private lateinit var appsAdapter : AvailableAppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddedAppsBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView(addedAppList)


        return view
    }

    // set up recyclerview for list of application
    private fun setupRecyclerView(appList: MutableList<String>){
        binding.rvAddedAppList.setHasFixedSize(true)
        binding.rvAddedAppList.setItemViewCacheSize(50)
        binding.rvAddedAppList.layoutManager = LinearLayoutManager(requireContext())
        appsAdapter = AvailableAppListAdapter(requireContext(), appList)
        binding.rvAddedAppList.adapter = appsAdapter
        appsAdapter.notifyDataSetChanged()
    }

}