package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // junk cleaner
        binding.junkCleaner.setOnClickListener {
            Toast.makeText(requireContext(), "Junk Cleaner", Toast.LENGTH_SHORT).show()
        }
        // network analysis
        binding.networkAnalysis.setOnClickListener {
            Toast.makeText(requireContext(), "Network Analysis", Toast.LENGTH_SHORT).show()
        }
        // booster
        binding.booster.setOnClickListener {
            Toast.makeText(requireContext(), "Booster", Toast.LENGTH_SHORT).show()
        }
        // app lock
        binding.cvAppLock.setOnClickListener {
            navigateToFragment(ApplicationsFragment())
        }
        // memory space
        binding.cvMemorySpace.setOnClickListener {
            Toast.makeText(requireContext(), "Memory Space", Toast.LENGTH_SHORT).show()
        }
        // storage space
        binding.cvStorage.setOnClickListener {
            Toast.makeText(requireContext(), "Storage Space", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    // change fragment
    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
    }

}