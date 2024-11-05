package com.example.my_veges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.my_veges.databinding.FragmentNotifactionBottomBinding
import com.example.my_veges.adapter.NotificationAdapter

class Notifaction_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotifactionBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotifactionBottomBinding.inflate(inflater, container, false)

        val notifications = listOf(
            "Your order has been Canceled Successfully",
            "Order has been taken by the driver",
            "Congratulations! Your order has been delivered"
        )
        val notificationImages = listOf(
            R.drawable.sad,
            R.drawable.deli,
            R.drawable.checked
        )

        // Create an instance of NotificationAdapter with the notifications and images
        val adapter = NotificationAdapter(ArrayList(notifications), ArrayList(notificationImages))

        // Set the adapter and layout manager for the RecyclerView
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter

        return binding.root
    }

    companion object {
        // Add any necessary companion object methods or variables here
    }
}
