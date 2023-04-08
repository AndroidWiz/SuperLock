package com.sk.superlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sk.superlock.databinding.ItemIntruderPhotoBinding
import com.sk.superlock.data.model.Intruder

class IntruderListAdapter(private val context: Context, private val intruderList: ArrayList<Intruder>): RecyclerView.Adapter<IntruderListAdapter.MyHolder>() {

    class MyHolder(binding: ItemIntruderPhotoBinding): RecyclerView.ViewHolder(binding.root){
        val photo = binding.ivIntruderImg
        val appIcon = binding.ivAppLogoIIP
        val appName = binding.tvAppNameIIP
        val time = binding.tvTimeIIP
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemIntruderPhotoBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return intruderList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val model = intruderList[position]

        holder.photo.setImageDrawable(model.intruderImage)
        holder.appName.text = model.appName
        holder.time.text = model.time
        holder.appIcon.setImageDrawable(model.appImage)
    }
}