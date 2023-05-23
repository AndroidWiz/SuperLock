package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sk.superlock.adapter.IntruderListAdapter
import com.sk.superlock.data.model.Intruder
import com.sk.superlock.databinding.FragmentSecurityBinding
import com.sk.superlock.util.PrefManager

class SecurityFragment : Fragment() {

    private lateinit var binding: FragmentSecurityBinding
    private lateinit var intruderListAdapter: IntruderListAdapter

    companion object {
        val intruderList: ArrayList<Intruder> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecurityBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (intruderList.size == 0) {
            binding.noIntruder.visibility = View.VISIBLE
            binding.rvIntruder.visibility = View.GONE
        } else {
            binding.noIntruder.visibility = View.GONE
            binding.rvIntruder.visibility = View.VISIBLE
        }

        setupRecyclerView()

    }

    // setup recyclerview
    private fun setupRecyclerView() {
        binding.rvIntruder.setItemViewCacheSize(20)
        binding.rvIntruder.setHasFixedSize(true)
        binding.rvIntruder.layoutManager = GridLayoutManager(requireContext(), 2)
        intruderListAdapter = IntruderListAdapter(requireContext(), intruderList)
        binding.rvIntruder.adapter = intruderListAdapter
        intruderListAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        val savedIntruderList = PrefManager(requireContext()).getIntruderList()
        intruderList.clear()
        intruderList.addAll(savedIntruderList)

        super.onResume()
    }

}