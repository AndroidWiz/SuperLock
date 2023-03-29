package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.R
import com.sk.superlock.databinding.ItemAvailableAppBinding

class AddedAppListAdapter(
    private val context: Context,
    private var addedAppList: MutableList<String>,
) : RecyclerView.Adapter<AddedAppListAdapter.MyHolder>() {

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

        holder.appName.text = model

        // setting the icon to the button depending on if added or not
//        var isAdded = false
        var isAdded = addedAppList.contains(model)
        holder.appStatus.setOnClickListener {
            //TODO: add the app to use lock and unlock method
            isAdded = !isAdded // toggle the value of isAdded
            if (isAdded) {
                addedAppList.remove(model)
                holder.appStatus.setImageResource(R.drawable.ic_add)
                Toast.makeText(context, "$model removed", Toast.LENGTH_SHORT).show()
            } else {
                addedAppList.add(model)
                holder.appStatus.setImageResource(R.drawable.ic_added)
                Toast.makeText(context, "$model added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // update list according to search
    fun updateList(newList: List<String>) {
        addedAppList = mutableListOf()
        addedAppList.addAll(newList)
        notifyDataSetChanged()
    }


}