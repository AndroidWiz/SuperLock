package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.FragmentLoginUsernameBinding

class LoginUsernameFragment : Fragment() {

    private lateinit var binding: FragmentLoginUsernameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginUsernameBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}