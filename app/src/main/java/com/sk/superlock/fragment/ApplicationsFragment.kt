package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.superlock.adapter.AvailableAppListAdapter
import com.sk.superlock.databinding.FragmentApplicationsBinding

class ApplicationsFragment : Fragment() {

    private lateinit var binding: FragmentApplicationsBinding
    private var availableAppList : MutableList<String> = mutableListOf()
    private lateinit var appsAdapter : AvailableAppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplicationsBinding.inflate(inflater, container, false)
        val view = binding.root

        availableAppList.add("WhatsApp")
        availableAppList.add("Twitter")
        availableAppList.add("Facebook")
        availableAppList.add("Messenger")
        availableAppList.add("WeChat")
        availableAppList.add("Fiverr")
        availableAppList.add("Upwork")

        setupRecyclerView()

        return view
    }

    // set up recyclerview for list of application
    private fun setupRecyclerView(){
        binding.rvAppList.setHasFixedSize(true)
        binding.rvAppList.setItemViewCacheSize(50)
        binding.rvAppList.layoutManager = LinearLayoutManager(requireContext())
        appsAdapter = AvailableAppListAdapter(requireContext(), availableAppList)
        binding.rvAppList.adapter = appsAdapter
        appsAdapter.notifyDataSetChanged()
    }

}