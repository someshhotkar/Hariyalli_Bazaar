package com.example.my_veges.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_veges.MenuBottomSheetFragment
import com.example.my_veges.R
import com.example.my_veges.adapter.MenuAdapter
import com.example.my_veges.adapter.PopularAdapter
import com.example.my_veges.databinding.FragmentHomeBinding
import com.example.my_veges.model.MenuItem
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        menuItems = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.ViewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "MenuBottomSheet")
        }

        retrieveAndDisplayPopularItems()

        return binding.root
    }

    private fun retrieveAndDisplayPopularItems() {
        // Get reference to the Firebase database
        val foodRef: DatabaseReference = database.reference.child("menu")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                // Display a random selection of popular items
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("HomeFragment", "Failed to retrieve data", error.toException())
            }
        })
    }

    private fun randomPopularItems() {
        // Shuffle the list of menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemsToShow = 6
        val subsetMenuItems = index.take(numItemsToShow).map { menuItems[it] }

        setPopularItemsAdapter(subsetMenuItems)
    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItems, requireContext())
        binding.PopulerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.PopulerRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example food names, prices, and images
        val foodName = listOf("Cauliflower", "Bhindi", "Spinach", "Onion")
        val price = listOf("70 Rs / Per Kg", "50 Rs / Per Kg", "80 Rs / Per Kg", "100 Rs / Per Kg")
        val popularFoodImages = listOf(R.drawable.cauli, R.drawable.bhindi, R.drawable.palak, R.drawable.onion)
    }
}
