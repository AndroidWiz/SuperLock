package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.R
import com.sk.superlock.databinding.ItemAvailableAppBinding
import com.sk.superlock.model.Applications

class AddedAppListAdapter(
    private val context: Context,
    private var addedAppList: MutableList<Applications>,
) :
    RecyclerView.Adapter<AddedAppListAdapter.MyHolder>() {

    class MyHolder(binding: ItemAvailableAppBinding) : RecyclerView.ViewHolder(binding.root) {
        val appIcon = binding.ivAvailableApp
        val appName = binding.tvAvailableAppName
        val appStatus = binding.ivAvailableAppStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemAvailableAppBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return addedAppList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val model = addedAppList[position]

        holder.appName.text = model.appName
        holder.appStatus.setImageResource(R.drawable.ic_added)
    }

}