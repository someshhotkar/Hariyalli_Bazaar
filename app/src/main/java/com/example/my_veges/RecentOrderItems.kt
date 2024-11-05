package com.example.my_veges

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_veges.adapter.RecentBuyAdapter
import com.example.my_veges.databinding.ActivityRecentOrderItemsBinding
import com.example.my_veges.model.OrderDetails

class RecentOrderItems : AppCompatActivity() {
    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }
    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }

        val recentOrderItems: ArrayList<OrderDetails>? =
            intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>

        recentOrderItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[0]

                // Initialize lists from the OrderDetails object
                allFoodNames = ArrayList(recentOrderItem.foodNames)
                allFoodImages = ArrayList(recentOrderItem.foodImages)
                allFoodPrices = ArrayList(recentOrderItem.foodPrices)
                allFoodQuantities = ArrayList(recentOrderItem.foodQuantities)
            } else {
                // Initialize lists as empty if no orders are present
                allFoodNames = ArrayList()
                allFoodImages = ArrayList()
                allFoodPrices = ArrayList()
                allFoodQuantities = ArrayList()
            }
        } ?: run {
            // Initialize lists as empty if no recentOrderItems were passed
            allFoodNames = ArrayList()
            allFoodImages = ArrayList()
            allFoodPrices = ArrayList()
            allFoodQuantities = ArrayList()
        }

        setAdapter()
    }

    private fun setAdapter() {
        val rv: RecyclerView = binding.recyclerView

        // Set up the layout manager
        rv.layoutManager = LinearLayoutManager(this)

        // Set up the adapter
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodImages, allFoodPrices, allFoodQuantities)
        rv.adapter = adapter
    }
}
