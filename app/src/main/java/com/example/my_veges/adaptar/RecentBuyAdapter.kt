package com.example.my_veges.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_veges.databinding.RecentBuyItemBinding

class RecentBuyAdapter(
    private val context: Context,
    private val foodNameList: ArrayList<String>,
    private val foodImageList: ArrayList<String>,
    private val foodPriceList: ArrayList<String>,
    private val foodQuantityList: ArrayList<Int>
) : RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding: RecentBuyItemBinding =
            RecentBuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int = foodNameList.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class RecentViewHolder(private val binding: RecentBuyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                foodName.text = foodNameList[position]
                foodPrice.text = foodPriceList[position]
                foodQuantity.text = foodQuantityList[position].toString()

                // Correct parsing of the URI
                val uriString: String = foodImageList[position]
                val uri: Uri = Uri.parse(uriString)

                // Using Glide to load the image
                Glide.with(context).load(uri).into(foodImage)
            }
        }
    }
}
