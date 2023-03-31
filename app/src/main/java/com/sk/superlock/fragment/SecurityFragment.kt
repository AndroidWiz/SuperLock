package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.FragmentSecurityBinding

class SecurityFragment : Fragment() {

    private lateinit var binding: FragmentSecurityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecurityBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}