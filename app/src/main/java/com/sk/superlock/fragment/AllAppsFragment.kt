package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.superlock.adapter.AvailableAppListAdapter
import com.sk.superlock.databinding.FragmentAllAppsBinding

class AllAppsFragment : Fragment() {

    private lateinit var binding: FragmentAllAppsBinding
    private var allAppList : MutableList<String> = mutableListOf()
    private lateinit var appsAdapter : AvailableAppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAppsBinding.inflate(inflater, container, false)
        val view = binding.root

        allAppList.add("WhatsApp")
        allAppList.add("Twitter")
        allAppList.add("Facebook")
        allAppList.add("Messenger")
        allAppList.add("WeChat")
        allAppList.add("Fiverr")
        allAppList.add("Upwork")

        setupRecyclerView(allAppList)

        return view
    }

    // set up recyclerview for list of application
    private fun setupRecyclerView(appList: MutableList<String>){
        binding.rvAppList.setHasFixedSize(true)
        binding.rvAppList.setItemViewCacheSize(50)
        binding.rvAppList.layoutManager = LinearLayoutManager(requireContext())
        appsAdapter = AvailableAppListAdapter(requireContext(), appList)
        binding.rvAppList.adapter = appsAdapter
        appsAdapter.notifyDataSetChanged()
    }

}