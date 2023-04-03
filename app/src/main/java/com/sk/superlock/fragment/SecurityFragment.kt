package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sk.superlock.R
import com.sk.superlock.adapter.IntruderListAdapter
import com.sk.superlock.databinding.FragmentSecurityBinding
import com.sk.superlock.model.Intruder

class SecurityFragment : Fragment() {

    private lateinit var binding: FragmentSecurityBinding
    private lateinit var intruderListAdapter : IntruderListAdapter

    companion object{
        val intruderList: ArrayList<Intruder> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecurityBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()

        return view
    }

    // setup recyclerview
    private fun setupRecyclerView(){
        binding.rvIntruder.setItemViewCacheSize(20)
        binding.rvIntruder.setHasFixedSize(true)
        binding.rvIntruder.layoutManager = GridLayoutManager(requireContext(), 2)
        intruderListAdapter = IntruderListAdapter(requireContext(), intruderList)
        binding.rvIntruder.adapter = intruderListAdapter
        intruderListAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        intruderList.clear()
        intruderList.add(Intruder(resources.getDrawable(R.drawable.logo), "WhatsApp", resources.getDrawable(R.drawable.logo), "02/04/2023 16:44"))
        intruderList.add(Intruder(resources.getDrawable(R.drawable.logo), "Instagram", resources.getDrawable(R.drawable.logo), "02/04/2023 16:45"))
        intruderList.add(Intruder(resources.getDrawable(R.drawable.logo), "Key Face", resources.getDrawable(R.drawable.logo), "02/04/2023 16:54"))


        super.onResume()
    }

}