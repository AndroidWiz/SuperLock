package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.R
import com.sk.superlock.databinding.ItemAvailableAppBinding

class AvailableAppListAdapter(private val context: Context, private val availableAppList: MutableList<String>): RecyclerView.Adapter<AvailableAppListAdapter.MyHolder>() {
//class AvailableAppListAdapter(private val context: Context, private val availableAppList: MutableList<ApplicationInfo>): RecyclerView.Adapter<AvailableAppListAdapter.MyHolder>() {

    class MyHolder(binding: ItemAvailableAppBinding): RecyclerView.ViewHolder(binding.root){
        val appIcon = binding.ivAvailableApp
        val appName = binding.tvAvailableAppName
        val appStatus = binding.ivAvailableAppStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemAvailableAppBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return availableAppList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val model = availableAppList[position]

        holder.appName.text = model

        // setting the icon to the button depending on if added or not
        var isAdded = false
        holder.appStatus.setOnClickListener {
            // TODO: add the app to use lock and unlock method
            isAdded = !isAdded // toggle the value of isAdded
            if(isAdded){
//                isAdded = true
                holder.appStatus.setImageResource(R.drawable.ic_added)
                Toast.makeText(context, "$model added", Toast.LENGTH_SHORT).show()
            }else{
//                isAdded = false
                holder.appStatus.setImageResource(R.drawable.ic_add)
                Toast.makeText(context, "$model removed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}