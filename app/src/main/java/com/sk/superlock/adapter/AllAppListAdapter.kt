package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.R
import com.sk.superlock.databinding.ItemAvailableAppBinding
import com.sk.superlock.data.model.Applications

class AllAppListAdapter(
    private val context: Context,
    private var allAppList: MutableList<Applications>,
    private val addedAppList: MutableList<Applications>,
    private val onAppAddedListener: OnAppAddedListener
) : RecyclerView.Adapter<AllAppListAdapter.MyHolder>() {

    interface OnAppAddedListener {
        fun onAppAdded(app: Applications)
        fun onAppRemoved(app: Applications)
    }

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
        return allAppList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val model = allAppList[position]

        holder.appName.text = model.appName

        // check if app has already been added
        if (addedAppList.contains(model)) {
            holder.appStatus.setImageResource(R.drawable.ic_added)
        } else {
            holder.appStatus.setImageResource(R.drawable.ic_add)
        }

        // add the app to use lock and unlock method
        holder.appStatus.setOnClickListener {
            if (addedAppList.contains(model)) {
                // remove app from addedAppList
                holder.appStatus.setImageResource(R.drawable.ic_add)
                onAppAddedListener.onAppRemoved(model)
            } else {
                // add app to addedAppList
                holder.appStatus.setImageResource(R.drawable.ic_added)
                onAppAddedListener.onAppAdded(model)
            }
        }
    }
}