package com.example.my_veges.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_veges.databinding.FragmentHistoryBinding
import com.example.my_veges.adapter.BuyAgainAdapter
import com.example.my_veges.model.OrderDetails
import com.example.my_veges.RecentOrderItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Initialize Firebase auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()

        // Retrieve and display the User's Order History
        retrieveBuyHistory()

        // Recent buy Button Click
        binding.recentBuyItem.setOnClickListener {
            seeItemsRecentBuy()
        }

        // Received button click for updating order status
        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }

        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey: String? = listOfOrderItem.firstOrNull()?.itemPushKey
        if (itemPushKey != null) {
            val completeOrderReference: DatabaseReference =
                database.reference.child("CompletedOrder").child(itemPushKey)
            completeOrderReference.child("paymentReceived").setValue(true)
        }
    }

    // Function to see recent buy
    private fun seeItemsRecentBuy() {
        val recentBuy = listOfOrderItem.firstOrNull()
        if (recentBuy != null) {
            val intent = Intent(requireContext(), RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem", recentBuy)
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        val userId = auth.currentUser?.uid ?: return
        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val sortingQuery: Query = buyItemReference.orderByChild("currentTime")

        sortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem: OrderDetails? = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }

                // Reverse the list to display the latest order first
                listOfOrderItem.reverse()

                if (listOfOrderItem.isNotEmpty()) {
                    // Display the most recent order details
                    setDataInRecentBuyItem()
                    // Set up the RecyclerView with previous order details
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    // Function to display the most recent order details
    private fun setDataInRecentBuyItem() {
        val recentOrderItem: OrderDetails? = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainFoodName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull() ?: ""
                val image: String = it.foodImages?.firstOrNull() ?: ""
                Glide.with(requireContext()).load(image).into(buyAgainFoodImage)

                val isOrderAccepted: Boolean = it.orderAccepted ?: false
                Log.d("TAG", "setDataInRecentBuyItem: $isOrderAccepted") // Corrected Log

                if (isOrderAccepted) {
                    orderdStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility = View.VISIBLE
                }
            }
        }
    }

    // Set up the RecyclerView with previous order details
    // Set up the RecyclerView with previous order details
    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodName: MutableList<String> = mutableListOf()
        val buyAgainFoodPrice: MutableList<String> = mutableListOf()
        val buyAgainFoodImage: MutableList<String> = mutableListOf()

        for (i in 1 until listOfOrderItem.size) {
            listOfOrderItem[i].foodNames?.firstOrNull()?.let {
                buyAgainFoodName.add(it)
            }
            listOfOrderItem[i].foodPrices?.firstOrNull()?.let {
                buyAgainFoodPrice.add(it)
            }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let {
                buyAgainFoodImage.add(it)
            }
        }

        // Pass requireContext() to the adapter
        val adapter = BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice, buyAgainFoodImage, requireContext())
        val rv: RecyclerView = binding.BuyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

}
