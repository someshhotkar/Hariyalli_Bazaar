package com.example.my_veges.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_veges.adapter.CartAdapter
import com.example.my_veges.databinding.FragmentCartBinding
import android.widget.Toast
import com.example.my_veges.PayOutActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.my_veges.model.CartItems

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        // Initialize the adapter with empty lists
        adapter = CartAdapter(
            requireContext(),
            mutableListOf(), // Empty list for foodNames
            mutableListOf(), // Empty list for foodPrices
            mutableListOf(), // Empty list for foodImagesUri
            mutableListOf(), // Empty list for foodDescriptions
            mutableListOf(), // Empty list for foodQuantities (as Int)
            mutableListOf()  // Empty list for foodIngredients (as String)
        )

        // Setup the RecyclerView
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter // Set the adapter here

        // Retrieve cart items from Firebase
        retrieveCartItems()

        // Set the proceed button to navigate to the PayOutActivity
        binding.proceedButton.setOnClickListener {
            getOrderItemsDetail()
        }

        return binding.root
    }

    private fun getOrderItemsDetail() {
        // Initialize Firebase reference for the order details
        val orderIdReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")

        val foodName: MutableList<String> = mutableListOf()
        val foodPrice: MutableList<String> = mutableListOf()
        val foodImage: MutableList<String> = mutableListOf()
        val foodDescription: MutableList<String> = mutableListOf()
        val foodIngredient: MutableList<String> = mutableListOf()

        // Get items Quantities
        val foodQuantities: MutableList<Int> = adapter.getUpdatedItemQuantities() // Changed to MutableList<Int>

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    // Get the CartItems object from the snapshot
                    val orderItems: CartItems? = foodSnapshot.getValue(CartItems::class.java)

                    // Add item details to the respective lists
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImageVal?.let { foodImage.add(it) }
                    orderItems?.foodIngredint?.let { foodIngredient.add(it) }
                }

                // After retrieving the details, navigate to the PayOutActivity
                orderNow(foodName, foodPrice, foodDescription, foodImage, foodIngredient, foodQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Order making failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int> // Changed to MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("FoodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("FoodItemQuantities", foodQuantities as ArrayList<Int>) // Changed to ArrayList<Int>

            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        // Initialize Firebase and database reference
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        val foodReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")

        // Initialize lists to store cart items
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        // Fetch data from the database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    // Get the CartItems object from the child node
                    val cartItems: CartItems? = foodSnapshot.getValue(CartItems::class.java)
                    // Add cart item details to lists
                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItems?.foodImageVal?.let { foodImagesUri.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) } // Ensure this is an Int
                }
                // Update the adapter after the data is fetched
                updateAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAdapter() {
        // Update the adapter with the retrieved data
        adapter = CartAdapter(
            requireContext(),
            foodNames,
            foodPrices,
            foodImagesUri,
            foodDescriptions,
            quantity,  // This should be a list of Int
            foodIngredients // Assuming ingredients are Strings
        )
        binding.cartRecyclerView.adapter = adapter // Update the adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
