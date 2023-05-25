package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.R
import com.sk.superlock.data.model.Apps
import com.sk.superlock.databinding.ItemAvailableAppBinding

class AppListAdapter(
    private val context: Context,
    private var allAppList: MutableList<Apps>,
    private val addedAppList: MutableList<Apps>,
    private val onAppAddedListener: OnAppAddedListener
) : RecyclerView.Adapter<AppListAdapter.MyHolder>() {

    interface OnAppAddedListener {
        fun onAppAdded(app: Apps)
        fun onAppRemoved(app: Apps)
    }

    class MyHolder(binding: ItemAvailableAppBinding) : RecyclerView.ViewHolder(binding.root) {
        val appIcon = binding.ivAvailableApp
        val appName = binding.tvAvailableAppName
        val appStatus = binding.ivAvailableAppStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemAvailableAppBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return allAppList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val model = allAppList[position]

        holder.appName.text = model.name
        holder.appIcon.setImageDrawable(model.icon)

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

        // set lock/unlock icon based on the lock status
        if (model.isLocked) {
            holder.appStatus.setImageResource(R.drawable.ic_added)
        } else {
            holder.appStatus.setImageResource(R.drawable.ic_add)
        }
    }

    fun lockApp(packageName: String) {
        val app = addedAppList.find { it.packageName == packageName }
        app?.let {
            it.isLocked = true
            notifyDataSetChanged()
        }
    }

    fun unlockApp(packageName: String) {
        val app = addedAppList.find { it.packageName == packageName }
        app?.let {
            it.isLocked = false
            notifyDataSetChanged()
        }
    }
}

/* fun lockApp(packageName: String) {
        val app = allAppList.find { it.packageName == packageName }
        app?.let {
            it.isLocked = true
            notifyDataSetChanged()
        }
    }

    fun unlockApp(packageName: String) {
        val app = allAppList.find { it.packageName == packageName }
        app?.let {
            it.isLocked = false
            notifyDataSetChanged()
        }
    }*/

/*
fun filterList(newList: List<Apps>) {
    allAppList = mutableListOf()
    allAppList.addAll(newList)
    notifyDataSetChanged()
}*/
