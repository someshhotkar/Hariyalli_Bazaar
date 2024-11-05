package com.example.my_veges

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.my_veges.databinding.ActivityDetailsBinding
import com.example.my_veges.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth
    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredient: String? = null
    private var foodPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Get the data from the intent
        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredient = intent.getStringExtra("MenuItemIngredients")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        // Bind the data to the views
        with(binding) {
            detailFoodName.text = foodName
            detailDescription.text = foodDescription
            detailIngredient.text = foodIngredient
        }

        // Load the image with Glide
        Glide.with(this)
            .load(Uri.parse(foodImage))
            .into(binding.detailFoodImage)

        // Set up back button listener
        binding.imageButton.setOnClickListener {
            finish()
        }

        // Set up Add to Cart button listener
        binding.addItemButton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        // Initialize Firebase Database reference
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val userId: String = auth.currentUser?.uid ?: ""

        // Create a CartItems object
        val cartItem = CartItems(
            foodName = foodName,
            foodPrice = foodPrice,
            foodDescription = foodDescription,
            foodImageVal = foodImage,
            foodQuantity = 1 // Assuming default quantity as 1
        )

        // Save cart item to Firebase Database
        database.child("user").child(userId).child("CartItems").push()
            .setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added to cart successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show()
            }
    }
}
