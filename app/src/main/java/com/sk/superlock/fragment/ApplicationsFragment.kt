package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.databinding.FragmentApplicationsBinding

class ApplicationsFragment : Fragment() {

    private lateinit var binding: FragmentApplicationsBinding
    private lateinit var filteredApList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplicationsBinding.inflate(inflater, container, false)
        val view = binding.root

        // search view
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                // handle query text changes
                filteredApList = mutableListOf()
//                if(newText != null){
//                    val userInput = newText.lowercase()
//                    for(app in allAppList){
//                        if(app.lowercase().contains(userInput)) filteredApList.add(app)
//                    }
//                    appsAdapter.updateList(filteredApList)
//                }
                return true
            }

        })

        setupChildFragment(AllAppsFragment())


        // radio button to show list of apps
        binding.rgAppType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_all_apps -> {
                    setupChildFragment(AllAppsFragment())
                }
                R.id.btn_added_apps -> {
                    setupChildFragment(AddedAppsFragment())
                }
            }
        }


        return view
    }

    // set up child fragments
    private fun setupChildFragment(fragment: Fragment){

        childFragmentManager.beginTransaction()
            .replace(binding.childAppsListFragmentHost.id, fragment)
            .commit()
    }

}