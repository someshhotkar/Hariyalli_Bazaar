package com.example.my_veges.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.my_veges.adapter.MenuAdapter
import com.example.my_veges.databinding.FragmentSearchBinding
import com.example.my_veges.model.MenuItem // Ensure MenuItem class is imported
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Retrieve menu items from database
        retrieveMenuItems()

        // Set up search view functionality
        setupSearchView()

        return binding.root
    }

    private fun retrieveMenuItems() {
        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()

        // Reference to the Menu node
        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem: MenuItem? = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors
            }
        })
    }

    private fun showAllMenu() {
        // Show all menu items by setting the adapter with the original list
        setAdapter(ArrayList(originalMenuItems))
    }

    private fun setAdapter(filteredMenuItems: List<MenuItem>) {
        adapter = MenuAdapter(filteredMenuItems, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    filterMenuItems(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterMenuItems(newText)
                }
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {
        // Filter menu items based on the search query
        val filteredMenuItems: List<MenuItem> = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        setAdapter(filteredMenuItems)
    }
}
