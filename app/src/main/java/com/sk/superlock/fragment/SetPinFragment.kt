package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.FragmentSetPinBinding

class SetPinFragment : Fragment() {

    private lateinit var binding: FragmentSetPinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetPinBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}